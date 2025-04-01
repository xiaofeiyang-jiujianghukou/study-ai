//package xiaofeiyang.study.ai.manager;
//
//import com.mysql.cj.protocol.Message;
//import jakarta.annotation.Resource;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Service
//public class DeepSeekChatService {
//
//    @Resource
//    private WebClient webClient;
//    /*@Resource
//    private SessionsService sessionsService;
//    @Resource*/
//    private MessagesService messagesService;
//
//    public Mono<String> generateResponse(Long sessionId, String userMessage) {
//        // 查找或创建会话
//        Sessions session = sessionsService.getById(sessionId);
//
//        // 获取对话历史
//        List<Messages> history = messagesService.findBySessionIdOrderByCreatedAt(sessionId);
//        List<Map<String, String>> messages = new ArrayList<>();
//
//        // 添加系统提示
//        Map<String, String> systemMessage = new HashMap<>();
//        systemMessage.put("role", "system");
//        systemMessage.put("content", "You are a helpful assistant.");
//        messages.add(systemMessage);
//
//        // 转换历史消息
//        for (Message msg : history) {
//            Map<String, String> message = new HashMap<>();
//            message.put("role", msg.getRole());
//            message.put("content", msg.getContent());
//            messages.add(message);
//        }
//
//        // 添加新的用户消息
//        Map<String, String> newUserMessage = new HashMap<>();
//        newUserMessage.put("role", "user");
//        newUserMessage.put("content", userMessage);
//        messages.add(newUserMessage);
//
//        // 构建请求体
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "deepseek-chat");
//        requestBody.put("stream", false);
//        requestBody.put("messages", messages);
//
//        // 发送请求并处理响应
//        return webClient.post()
//                .bodyValue(requestBody)
//                .header("Authorization", "Bearer " + apiKey)
//                .retrieve()
//                .bodyToMono(String.class)
//                .map(response -> {
//                    String aiResponse = parseResponse(response);
//                    // 保存新消息到数据库
//                    saveMessage(sessionId, "user", userMessage);
//                    saveMessage(sessionId, "assistant", aiResponse);
//                    return aiResponse;
//                });
//    }
//
//    private void saveMessage(Long sessionId, String role, String content) {
//        Session session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new RuntimeException("Session not found"));
//        Message message = new Message();
//        message.setSession(session);
//        message.setRole(role);
//        message.setContent(content);
//        messageRepository.save(message);
//    }
//
//    private String parseResponse(String response) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(response);
//            return jsonNode.get("choices").get(0).get("message").get("content").asText();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse DeepSeek response: " + e.getMessage(), e);
//        }
//    }
//
//    // 为游客创建临时会话
//    public Session createGuestSession() {
//        Session session = new Session();
//        session.setSessionToken(UUID.randomUUID().toString()); // 随机生成游客会话标识
//        return sessionRepository.save(session);
//    }
//
//    // 为登录用户创建会话
//    public Session createUserSession(Long userId, String name) {
//        Session session = new Session();
//        session.setUser(new User(userId)); // 假设 User 有构造函数或 setter
//        session.setName(name);
//        return sessionRepository.save(session);
//    }
//}
