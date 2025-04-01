//package xiaofeiyang.study.ai.model;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//public class DeepSeekChatModel implements ChatModel {
//    private final WebClient webClient;
//
//    public DeepSeekChatModel(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("https://api.deepseek.com/chat/completions").build();
//    }
//
//    @Override
//    public Mono<ChatResponse> call(ChatClient.ChatClientRequestSpec request) {
//        // 构建请求体，调用DeepSeek API，解析响应
//        return webClient.post()
//                .bodyValue(buildDeepSeekRequest(request))
//                .retrieve()
//                .bodyToMono(String.class)
//                .map(this::parseResponse);
//    }
//
//    private Map<String, Object> buildDeepSeekRequest(ChatRequest request) {
//        // 转换为DeepSeek要求的JSON结构
//        Map<String, Object> body = new HashMap<>();
//        body.put("model", "deepseek-chat");
//        body.put("stream", false);
//        // 填充messages...
//        return body;
//    }
//
//    private ChatResponse parseResponse(String response) {
//        // 解析DeepSeek的JSON响应，转换为Spring AI的ChatResponse
//        return new ChatResponse(/* 填充内容 */);
//    }
//}
