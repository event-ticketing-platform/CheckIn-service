package ee.ut.eventticketing.checkin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        Object rolesClaim = jwt.getClaim("roles");
        if (rolesClaim instanceof Collection<?> collection) {
            collection.forEach(value -> roles.add(String.valueOf(value)));
        } else if (rolesClaim instanceof String role) {
            roles.add(role);
        }

        Object roleClaim = jwt.getClaim("role");
        if (roleClaim instanceof String role) {
            roles.add(role);
        }

        Object authoritiesClaim = jwt.getClaim("authorities");
        if (authoritiesClaim instanceof Collection<?> collection) {
            collection.forEach(value -> roles.add(String.valueOf(value)));
        }

        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(this::toAuthority)
                .map(SimpleGrantedAuthority::new)
                .<GrantedAuthority>map(a -> a)
                .toList();
    }

    private String toAuthority(String role) {
        String normalized = role.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }
}
