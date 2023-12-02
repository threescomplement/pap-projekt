package pl.edu.pw.pap.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtToPrincipalConverter {
    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(Long.valueOf(jwt.getSubject()))
                .username(stripQuotationMarks(String.valueOf(jwt.getClaim("u"))))
                .email(stripQuotationMarks(String.valueOf(jwt.getClaim("e"))))
                .authorities(extractAuthoritiesFromClaim(jwt))
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt) {
        var claim = jwt.getClaim("a");
        if (claim.isNull() || claim.isMissing()) {
            return List.of();
        }

        return claim.asList(SimpleGrantedAuthority.class);
    }

    private String stripQuotationMarks(String text) {
        return text.replace("\"", "");
    }
}
