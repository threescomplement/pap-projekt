package pl.edu.pw.pap.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("email-verification")
public class EmailSenderProperties {
    private String confirmBaseUrl;
    private String resetPasswordBaseUrl;
    @Value("${spring.mail.username}")
    private String senderEmail;
}
