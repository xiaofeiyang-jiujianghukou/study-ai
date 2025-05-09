package xiaofeiyang.study.ai.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DeepSeekClientConfig extends CommonClientConfig {

    @Value("${deepseek.api.url}")
    private String API_URL;

    @Value("${deepseek.api.key}")
    private String API_KEY;

    @Value("${deepseek.api.model}")
    private String API_MODEL;

    @Bean("deepSeekClient")
    @Primary
    public WebClient deepSeekClient() {
        // 创建 WebClient 实例
        WebClient webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .build();
        return webClient;
    }

}
