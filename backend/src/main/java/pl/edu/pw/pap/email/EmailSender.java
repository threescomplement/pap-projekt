package pl.edu.pw.pap.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;
    private final EmailSenderProperties properties;
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    public void sendEmail(String toAddress, String content, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getSenderEmail());
        message.setTo(toAddress);
        message.setText(content);
        message.setSubject(subject);
        javaMailSender.send(message);
        log.info(String.format("Sent message: %s", message));
    }


}
