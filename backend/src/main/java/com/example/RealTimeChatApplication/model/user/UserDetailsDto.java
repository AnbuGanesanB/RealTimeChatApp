package com.example.RealTimeChatApplication.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {

    private int id;
    private String emailId;
    private String name;
    private String recipientType;

    private String dpPath;
    private boolean isDpAvailable;
    private String onlineStatus;
    private String initials;
}
