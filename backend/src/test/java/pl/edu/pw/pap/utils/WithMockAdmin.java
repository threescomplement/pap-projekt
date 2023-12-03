package pl.edu.pw.pap.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Alias for `@WithMockUser` with admin privileges
 */
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(authorities = "ROLE_ADMIN")
public @interface WithMockAdmin {
}
