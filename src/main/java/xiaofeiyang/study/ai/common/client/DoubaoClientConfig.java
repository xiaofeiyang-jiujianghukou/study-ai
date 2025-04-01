package xiaofeiyang.study.ai.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DoubaoClientConfig extends CommonClientConfig {

    @Value("${doubao.api.url}")
    private String API_URL;

    @Value("${doubao.api.key}")
    private String API_KEY;

    @Value("${doubao.api.model}")
    private String API_MODEL;

    @Bean("doubaoClient")
    public WebClient qwenClient() {
        // 创建 WebClient 实例
        WebClient webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .build();
        return webClient;
    }

}
