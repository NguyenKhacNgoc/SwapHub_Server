package com.server.ecommerce.Config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // private final String[] PUBLIC_ENDPOINT = { "/api/signup", "/api/auth/login"
    // };
    // private final String[] ADMIN_ENDPOINT = { "/api/getUsers" };
    // private final String[] SWAGGER_ENDPOINT = { "/swagger-ui/index.html",
    // "/swagger-ui/swagger-ui.css",
    // "/swagger-ui/index.css", "/swagger-ui/swagger-ui-bundle.js",
    // "/swagger-ui/swagger-ui-standalone-preset.js",
    // "/swagger-ui/swagger-initializer.js",
    // "/swagger-ui/favicon-32x32.png", "/swagger-ui/favicon-16x16.png",
    // "/v3/api-docs/swagger-config", "/v3/api-docs" };

    @Value("${jwt.signerkey}")
    private String signerkey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                request -> request.requestMatchers(new AntPathRequestMatcher("/api/signup"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        // .requestMatchers(HttpMethod.GET, SWAGGER_ENDPOINT)
                        // .permitAll()
                        // .requestMatchers(HttpMethod.GET, ADMIN_ENDPOINT).hasAuthority("SCOPE_ADMIN")
                        .anyRequest().authenticated());
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerkey.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();

    }

}
