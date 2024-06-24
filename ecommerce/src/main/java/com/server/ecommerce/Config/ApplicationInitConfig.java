package com.server.ecommerce.Config;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Enums.Role;
import com.server.ecommerce.Repository.UserRepository;

@Configuration
public class ApplicationInitConfig {
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return arg -> {
            if (userRepository.findByEmail("admin").isEmpty()) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                HashSet<String> roles = new HashSet<>();
                roles.add(Role.ADMIN.name());
                userRepository.save(User.builder()
                        .email("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(roles)
                        .build());
            }

        };
    }

}
