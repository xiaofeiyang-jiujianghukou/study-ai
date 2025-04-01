package xiaofeiyang.study.ai.common.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Getter
public class CommonClientConfig {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    @Value("${deepseek.api.key}")
    private String API_KEY;

    @Value("${deepseek.api.model}")
    private String API_MODEL;

}
