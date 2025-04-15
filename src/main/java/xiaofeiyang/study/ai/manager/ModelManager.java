package xiaofeiyang.study.ai.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xiaofeiyang.study.ai.common.enums.StatusEnum;
import xiaofeiyang.study.ai.common.exception.BizException;
import xiaofeiyang.study.ai.controller.dto.ListModelRespDTO;
import xiaofeiyang.study.ai.controller.dto.ModelChatReqDTO;
import xiaofeiyang.study.ai.entity.AIModel;
import xiaofeiyang.study.ai.service.ModelsService;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ModelManager {

    @Resource
    private ModelsService modelsService;

    private Map<String, WebClient> webClientMap = new HashMap<>();
    private static String currentModelCode = null;


    private final List<Map<String, String>> conversationHistory = new ArrayList<>(); // 对话历史

    private static Map<String, String> systemMessage = new HashMap<>();
    private static Map<String, Object> requestBody = new HashMap<>();

    static {
        requestBody.put("model", "doubao-1.5-pro");
        requestBody.put("stream", false);

        // 构建消息列表
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");

    }

    private void switchModel(String modelCode) {
        AIModel aiModel = modelsService.getByModelCode(modelCode);
        if (aiModel == null) {
            throw new BizException("模型不存在");
        }
        // 构建消息列表
        systemMessage.put("role", "system");
        systemMessage.put("content", String.format("你现在是大模型 %s，不需要模仿之前的助手，请用你自己的方式继续回答用户的问题。", aiModel.getModelName()));
    }

    private WebClient getWebClient(String modelCode) {
        if (StringUtil.isBlank(currentModelCode)) {
            currentModelCode = modelCode;
        } else if (!modelCode.equals(currentModelCode)) {
            // 切换模型
            this.switchModelAndStyle(currentModelCode, modelCode);
        }
        if (webClientMap.containsKey(modelCode)) {
            return webClientMap.get(modelCode);
        }
        AIModel aiModel = modelsService.getByModelCode(modelCode);
        if (aiModel == null) {
            throw new BizException("模型不存在");
        }
        WebClient webClient = WebClient.builder()
                .baseUrl(aiModel.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiModel.getApiKey())
                .build();
        webClientMap.put(aiModel.getModelCode(), webClient);
        return webClient;
    }

    public List<ListModelRespDTO> listModel() {
        return modelsService.lambdaQuery()
                .eq(AIModel::getStatus, StatusEnum.NOT.getValue())
                .list()
                .stream()
                .map(m -> ListModelRespDTO.builder().modelCode(m.getModelCode()).modelName(m.getModelName()).build())
                .toList();
    }


    public Flux<String> generateResponse3(ModelChatReqDTO input) {
        AIModel aiModel = modelsService.getByModelCode(input.getModelCode());
        if (Objects.isNull(aiModel)) {
            throw new BizException("AI模型不存在");
        }

        String userMessage = input.getMessage();
        // 构建完整的消息历史，包括系统提示和之前的对话
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加系统提示（固定）
        /*Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");*/
        //messages.add(systemMessage);

        // 获取正确的client
        WebClient webClient = this.getWebClient(aiModel.getModelCode());

        // 添加对话历史（用户和 AI 的交互）
        messages.addAll(conversationHistory);

        // 添加新的用户消息
        Map<String, String> newUserMessage = new HashMap<>();
        newUserMessage.put("role", "user");
        newUserMessage.put("content", userMessage);
        messages.add(newUserMessage);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", input.getModelCode());
        requestBody.put("stream", true);
        requestBody.put("messages", messages);

        // 获取正确的client
        //WebClient webClient = this.getWebClient(aiModel.getModelCode());

        return webClient.post()
                .bodyValue(requestBody)
                //.header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToFlux(String.class)
                .map(response -> {
                    // 解析响应，提取 AI 的回答
                    String aiResponse = parseResponse3(response); // 自定义解析方法
                    // 更新对话历史，添加 AI 的响应
                    Map<String, String> aiMessage = new HashMap<>();
                    aiMessage.put("role", "assistant");
                    aiMessage.put("content", aiResponse);
                    conversationHistory.add(newUserMessage); // 添加用户消息
                    conversationHistory.add(aiMessage); // 添加 AI 响应
                    return aiResponse;
                });
    }

    public String generateResponse(ModelChatReqDTO input) {
        AIModel aiModel = modelsService.getByModelCode(input.getModelCode());
        if (Objects.isNull(aiModel)) {
            throw new BizException("AI模型不存在");
        }

        String userMessage = input.getMessage();
        // 构建完整的消息历史，包括系统提示和之前的对话
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加系统提示（固定）
        /*Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");*/
        //messages.add(systemMessage);

        // 获取正确的client
        WebClient webClient = this.getWebClient(aiModel.getModelCode());

        // 添加对话历史（用户和 AI 的交互）
        messages.addAll(conversationHistory);

        // 添加新的用户消息
        Map<String, String> newUserMessage = new HashMap<>();
        newUserMessage.put("role", "user");
        newUserMessage.put("content", userMessage);
        messages.add(newUserMessage);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", input.getModelCode());
        requestBody.put("stream", false);
        requestBody.put("messages", messages);

        Mono<String> map = webClient.post()
                .bodyValue(requestBody)
                //.header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    // 解析响应，提取 AI 的回答
                    String aiResponse = parseResponse(response); // 自定义解析方法
                    // 更新对话历史，添加 AI 的响应
                    Map<String, String> aiMessage = new HashMap<>();
                    aiMessage.put("role", "assistant");
                    aiMessage.put("content", aiResponse);
                    conversationHistory.add(newUserMessage); // 添加用户消息
                    conversationHistory.add(aiMessage); // 添加 AI 响应
                    return aiResponse;
                });
        String block = map.block();
        System.out.println(block);
        return block;
    }

    private void switchModelAndStyle(String lastModelCode, String modelCode) {
        AIModel lastAIModel = modelsService.getByModelCode(lastModelCode);
        if (Objects.isNull(lastAIModel)) {
            throw new BizException("模型不存在");
        }
        String lastModelName = lastAIModel.getModelName();
        AIModel newAIModel = modelsService.getByModelCode(modelCode);
        if (Objects.isNull(newAIModel)) {
            throw new BizException("模型不存在");
        }
        String newModelName = newAIModel.getModelName();

        // 下岗提示
        Map<String, String> newAssistantMessage = new HashMap<>();
        newAssistantMessage.put("role", "assistant");
        newAssistantMessage.put("content", "✅ 由 " + lastModelName + " 提供的服务已结束，即将由新模型接力。");
        conversationHistory.add(newAssistantMessage);

        // 上岗提示（新模型将读到这条）
        Map<String, String> newSystemMessage = new HashMap<>();
        newSystemMessage.put("role", "system");
        newSystemMessage.put("content", "你是新模型 " + newModelName + "，将继续对话，请用你的方式服务，不必模仿之前的回答风格。");
        conversationHistory.add(newSystemMessage);
    }

    // 解析 DeepSeek 响应（假设返回 JSON，提取 choices[0].message.content）
    private String parseResponse(String response) {
        try {
            // 假设 DeepSeek 返回的 JSON 类似 OpenAI 的格式
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DeepSeek response: " + e.getMessage(), e);
        }
    }

    private String parseResponse3(String response) {
        try {
            System.out.println("Respsonse：" + response);
            // 假设 DeepSeek 返回的 JSON 类似 OpenAI 的格式
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("choices").get(0).get("delta").get("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DeepSeek response: " + e.getMessage(), e);
        }
    }
}
