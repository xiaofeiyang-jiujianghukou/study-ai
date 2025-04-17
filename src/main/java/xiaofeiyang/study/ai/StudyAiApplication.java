package xiaofeiyang.study.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableConfigurationProperties
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class StudyAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyAiApplication.class, args);
	}

}
