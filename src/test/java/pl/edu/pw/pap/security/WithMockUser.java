package pl.edu.pw.pap.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate test methods with this when testing secured API endpoints.
 * Provides default values, may be overridden
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface WithMockUser {
    long userId() default 1L;

    String username() default "user";
    String[] authorities() default "ROLE_USER";
}
