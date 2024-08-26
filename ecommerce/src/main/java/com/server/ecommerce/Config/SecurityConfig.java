package com.server.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
        private String issuerUrl;
        private final String[] PUBLIC_ENDPOINT = { "/api/register", "api/category" };

        @Bean
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
                return new NullAuthenticatedSessionStrategy();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                                .requestMatchers("/api/users").hasRole("get_users")
                                .requestMatchers("/api/user/delete/{id}").hasRole("delete_user")
                                .requestMatchers("/api/role/create").hasRole("create_role")
                                .requestMatchers("/api/role/delete/{roleName}").hasRole("delete_role")
                                .requestMatchers("/api/role/assign/user/{userId}").hasRole("assign_role")
                                .requestMatchers("/api/role/associate/{roleName}").hasRole("assign_role")
                                .requestMatchers("api/role/associate/remove/{roleName}").hasRole("remove_role")
                                .requestMatchers("/api/role/remove/user/{userId}").hasRole("remove_role")
                                .requestMatchers("/api/roles").hasRole("get_roles")
                                .requestMatchers("/api/roles/{userId}").hasRole("get_roles")
                                .requestMatchers("/api/role/composite/*").hasRole("get_roles")
                                .requestMatchers(HttpMethod.GET, "/api/post").hasAnyRole("get_post")
                                .requestMatchers(HttpMethod.GET, "/api/post/*").hasAnyRole("get_post")
                                .requestMatchers(HttpMethod.POST, "/api/post/*").hasRole("create_post")
                                .requestMatchers(HttpMethod.PUT, "/api/post/*").hasAnyRole("update_post")
                                .anyRequest().authenticated())
                                .csrf(AbstractHttpConfigurer::disable)
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter())));
                return http.build();
        }

        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
                return jwtAuthenticationConverter;
        }

        @Bean
        JwtDecoder jwtDecoder() {
                return JwtDecoders.fromOidcIssuerLocation(issuerUrl);
        }

}
