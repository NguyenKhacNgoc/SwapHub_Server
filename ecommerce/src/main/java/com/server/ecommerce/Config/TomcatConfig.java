package com.server.ecommerce.Config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class TomcatConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set the maximum file size and request size using DataSize
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // Example: 10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); // Example: 10MB
        return factory.createMultipartConfig();
    }

}
