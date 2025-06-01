package com.example.RealTimeChatApplication.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @JsonProperty("register_emailId")
    private String emailId;
    @JsonProperty("register_password")
    private String password;
    @JsonProperty("register_name")
    private String userName;
}
