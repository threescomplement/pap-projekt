package pl.edu.pw.pap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import pl.edu.pw.pap.config.AppConfiguration;
import pl.edu.pw.pap.security.JwtProperties;
import pl.edu.pw.pap.utils.DummyData;

@SpringBootApplication
public class PapApplication {

    private static final Logger log = LoggerFactory.getLogger(PapApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PapApplication.class, args);
    }

    @Bean
    public CommandLineRunner reportStatus(
            JwtProperties jwtProperties,
            AppConfiguration appConfiguration
    ) {
        return (args) -> {
            if (jwtProperties.getSecretKey() != null) {
                log.info("Loaded secret key for signing JWT tokens");
            } else {
                log.error("Missing JWT signing secret key");
            }

            log.info(String.format("Frontend app available on %s", appConfiguration.getWebsiteBaseUrl()));
            log.info(String.format("Schedule for deleting expired tokens (cron): %s", appConfiguration.getDeleteTokensCronExpression()));
        };
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner addDummyData(DummyData generator) {
        return (args) -> {
            generator.addDummyData();
            log.info("Added dummy data to the database");
        };
    }
}
