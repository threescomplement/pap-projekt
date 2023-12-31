package pl.edu.pw.pap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Getter
@Setter
@EnableScheduling
@Configuration
@ConfigurationProperties("app")
public class AppConfiguration {
    private String deleteTokensCronExpression;
}
