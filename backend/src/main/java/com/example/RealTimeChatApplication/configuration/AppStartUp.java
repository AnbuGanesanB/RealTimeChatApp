package com.example.RealTimeChatApplication.configuration;

import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.message.MessageType;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.MessageRepo;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import com.example.RealTimeChatApplication.service.MessageService;
import com.example.RealTimeChatApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class AppStartUp {

    private final UserRepo userRepo;
    private final UserService userService;
    private final MessageRepo messageRepo;

    /*@Bean
    public CommandLineRunner migratePasswords() {
        return args -> {

            userRepo.findAll().forEach(user -> {
                String plainPassword = user.getPassword();

                if (!plainPassword.startsWith("$2a$")) {
                    String encoded = new BCryptPasswordEncoder().encode(plainPassword);
                    user.setPassword(encoded);
                    userRepo.save(user);
                    System.out.println("Migrated password for: " + user.getUsername());
                }
            });

            System.out.println("Password migration completed.");
        };
    }*/

}
