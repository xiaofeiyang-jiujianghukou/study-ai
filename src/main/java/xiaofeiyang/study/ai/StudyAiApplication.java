package xiaofeiyang.study.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class StudyAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyAiApplication.class, args);
	}

}
