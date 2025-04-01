package xiaofeiyang.study.ai.deepseek;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeepSeekService {

    @Resource
    private WebClient webClient;

    public Mono<String> generateResponse(String userMessage) {
        Map<String, Object> requestBody = buildRequestBody(userMessage);
        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    private Map<String, Object> buildRequestBody(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("stream", false);

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);

        requestBody.put("messages", new Object[]{systemMessage, userMsg});
        return requestBody;
    }
}
