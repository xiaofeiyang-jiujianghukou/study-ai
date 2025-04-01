package xiaofeiyang.study.ai.deepseek;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeepSeekApiClient {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String API_KEY = "sk-bed67c0c95c04370b92986264ccf21c7"; // 替换为你的 DeepSeek API 密钥

    private final WebClient webClient;
    private final String apiKey;
    private final List<Map<String, String>> conversationHistory = new ArrayList<>(); // 对话历史

    public DeepSeekApiClient(WebClient.Builder webClientBuilder, @Value("${deepseek.api.key}")String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.deepseek.com/chat/completions").build();
        this.apiKey = apiKey;
    }

    public Mono<String> generateResponse(String userMessage) {
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
        return webClient.post()
                .bodyValue(requestBody)
                .header("Authorization", "Bearer " + apiKey)
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

    // 测试方法（更新 main 方法）
    public static void main(String[] args) {
        SpringApplication.run(DeepSeekApiClient.class, args)
                .getBean(DeepSeekApiClient.class)
                .generateResponse("Hello!")
                .subscribe(
                        response -> System.out.println("DeepSeek Response: " + response),
                        error -> System.err.println("Error: " + error.getMessage())
                );

        // 模拟连续对话（例如 5 秒后发送第二个问题）
        try {
            Thread.sleep(5000);
            SpringApplication.run(DeepSeekApiClient.class, args)
                    .getBean(DeepSeekApiClient.class)
                    .generateResponse("What is the capital of France?")
                    .subscribe(
                            response -> System.out.println("DeepSeek Response: " + response),
                            error -> System.err.println("Error: " + error.getMessage())
                    );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000); // 等待所有响应
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
