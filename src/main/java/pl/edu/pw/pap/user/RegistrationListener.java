package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final Logger log = LoggerFactory.getLogger(RegistrationListener.class);
    private final UserService userService;
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        log.info("Processing registration event for user " + event.getUser().getUsername());
        var user = event.getUser();
        var token = userService.generateVerificationToken(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(user.getEmail());
        message.setSubject("Verify your email");
        message.setText("Verification token is: " + token.getToken());  // TODO: link to appropriate frontend page
        emailSender.send(message);
        log.info("Sent email: " + message.toString());
    }
}
