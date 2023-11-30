package pl.edu.pw.pap.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.security.UserPrincipalAuthenticationToken;

import java.util.Arrays;

/**
 * Make custom annotations work with the Spring Security and testing ecosystems
 */
public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockUser annotation) {
        var authorities = Arrays.stream(annotation.authorities())
                .map(SimpleGrantedAuthority::new)
                .toList();
        var principal = UserPrincipal.builder()
                .userId(annotation.userId())
                .username(annotation.username())
                .authorities(authorities)
                .build();
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UserPrincipalAuthenticationToken(principal));
        return context;
    }
}
