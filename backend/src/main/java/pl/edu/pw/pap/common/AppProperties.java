package pl.edu.pw.pap.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app")
public class AppProperties {
    private String websiteBaseUrl;
    private String confirmEmailUrl;
    private String resetPasswordUrl;
}
