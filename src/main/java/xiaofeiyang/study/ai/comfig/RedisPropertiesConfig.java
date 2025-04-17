package xiaofeiyang.study.ai.comfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisPropertiesConfig {

    private int timeout = 3000;

    private String host;
    private String port;
    private Integer database;

    private String password;

    private int connectionPoolSize = 10;

    private int connectionMinimumIdleSize = 10;

    private int slaveConnectionPoolSize = 10;

    private int masterConnectionPoolSize = 10;

    private String[] sentinelAddresses;

    private String masterName;


    public void setSentinelAddresses(String sentinelAddresses) {
        this.sentinelAddresses = sentinelAddresses.split(",");
    }

    @PostConstruct
    public void init() {
        System.out.println("Redis Host: " + host);
        System.out.println("Redis Port: " + port);
        System.out.println("Redis Password: " + password);
    }

}
