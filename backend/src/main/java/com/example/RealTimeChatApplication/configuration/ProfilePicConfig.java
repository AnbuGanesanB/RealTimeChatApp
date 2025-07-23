package com.example.RealTimeChatApplication.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.util.List;

@Configuration
public class ProfilePicConfig {

    private final DataSize maxSize = DataSize.ofMegabytes(2);
    private final List<String> allowedTypes = List.of("image/jpeg", "image/png");

    public DataSize getMaxSize() {
        return maxSize;
    }

    public List<String> getAllowedTypes() {
        return allowedTypes;
    }
}
