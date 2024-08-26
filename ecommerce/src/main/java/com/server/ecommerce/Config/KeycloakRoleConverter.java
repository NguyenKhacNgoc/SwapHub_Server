package com.server.ecommerce.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @SuppressWarnings({ "null", "unchecked" })
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        return ((List<String>) realmAccess.get("roles")).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Ánh xạ với tiền tố "ROLE_"
                .collect(Collectors.toList());

    }
}
