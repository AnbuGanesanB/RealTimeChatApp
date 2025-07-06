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

    /*private LocalDateTime truncateToMicros(LocalDateTime dateTime) {
        return dateTime.withNano((dateTime.getNano() / 1000) * 1000);
    }*/

    /*@Bean
    public CommandLineRunner testMessage() {
        return args -> {

            System.out.println("Starting command Line runner-");
            User user1 = userService.getUserById(1);
            User user2 = userService.getUserById(2);
            Message message = new Message();
            message.setSender(user1);
            message.setIndRecipient(user2);
            message.setMessageType(MessageType.TEXT_MESSAGE);
            message.setContent("Trial 6");

            LocalDateTime localTime = LocalDateTime.now();
            System.out.println("Before truncate localTime:"+localTime);
            localTime = truncateToMicros(localTime);
            System.out.println("After truncate localTime:"+localTime);
            message.setTimestamp(localTime);

            Instant instantTime = Instant.now();
            System.out.println("Instant:"+instantTime);
            message.setTimestamp2(Instant.now());

            messageRepo.save(message);
        };
    }*/

}
