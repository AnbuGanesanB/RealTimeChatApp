package com.example.RealTimeChatApplication.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoggedUserInfoDto {

    private int userId;
    private String emailId;
    private String name;
    private String dpPath;
    private boolean isDpAvailable;
    private String aboutMe;
    private String onlineStatus;
    private String initials;
}
