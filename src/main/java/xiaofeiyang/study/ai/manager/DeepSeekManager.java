package xiaofeiyang.study.ai.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import xiaofeiyang.study.ai.controller.dto.ChatReqDTO;
import xiaofeiyang.study.ai.controller.dto.ChatRespDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DeepSeekManager {

    private final List<Map<String, String>> conversationHistory = new ArrayList<>(); // 对话历史

    @Resource
    @Qualifier(value = "deepSeekClient")
    private WebClient deepSeekClient;

    private static Map<String, String> systemMessage = new HashMap<>();
    private static Map<String, Object> requestBody = new HashMap<>();

    static {
        requestBody.put("model", "deepseek-chat");
        requestBody.put("stream", false);

        // 构建消息列表
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");

    }

    public ChatRespDTO chat(ChatReqDTO input) {

        // 构建请求体

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", input.getMessage());

        requestBody.put("messages", new Object[]{systemMessage, userMessage});

        // 发送 POST 请求并处理响应
        Mono<String> responseMono = deepSeekClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);

        // 订阅并打印结果（阻塞方式，适合简单测试）
        String response = responseMono.block();
        System.out.println("DeepSeek Response: " + response);

        return ChatRespDTO.builder().message(response).build();
        // 或者非阻塞方式（推荐生产环境）
        /*
        responseMono.subscribe(
            result -> System.out.println("DeepSeek Response: " + result),
            error -> System.err.println("Error: " + error.getMessage())
        );
        */
    }

    public String generateResponse(String userMessage) {
        // 构建完整的消息历史，包括系统提示和之前的对话
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加系统提示（固定）
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messages.add(systemMessage);

        // 添加对话历史（用户和 AI 的交互）
        messages.addAll(conversationHistory);

        // 添加新的用户消息
        Map<String, String> newUserMessage = new HashMap<>();
        newUserMessage.put("role", "user");
        newUserMessage.put("content", userMessage);
        messages.add(newUserMessage);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("stream", false);
        requestBody.put("messages", messages);

        // 发送请求并处理响应
        /*return deepSeekClient.post()
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
                });*/
        Mono<String> map = deepSeekClient.post()
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

    // 可选：限制对话历史长度，防止超出上下文窗口
    private void limitConversationHistory(int maxMessages) {
        if (conversationHistory.size() > maxMessages) {
            conversationHistory.subList(0, conversationHistory.size() - maxMessages).clear();
        }
    }

    // 示例方法：清空对话历史（如果需要）
    public void clearConversationHistory() {
        conversationHistory.clear();
    }
}
