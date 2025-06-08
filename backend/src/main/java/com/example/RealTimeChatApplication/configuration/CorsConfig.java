package com.example.RealTimeChatApplication.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class CorsConfig implements CorsConfigurationSource {

    @Value("${frontend.allowed-origin}")
    private String allowedFrontEndPort;

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        String origin = request.getHeader("Origin");

        if (origin != null && origin.equals(allowedFrontEndPort)) {
            corsConfiguration.setAllowedOrigins(List.of(allowedFrontEndPort));
        }else if (origin != null) {
            System.out.println("Request is coming from "+origin+" instead of Authorised origin " + allowedFrontEndPort);
            throw new RuntimeException("CORS violation: Request from unauthorized origin: " + origin);
        }
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }
}

