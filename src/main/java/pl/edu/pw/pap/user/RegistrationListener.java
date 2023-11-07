package pl.edu.pw.pap.user;

import org.springframework.context.ApplicationListener;

public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final UserService userService;
}
