package pl.edu.pw.pap.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("email-verification")
public class EmailSenderProperties {
    private String confirmBaseUrl;
}
