package com.example.RealTimeChatApplication.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class RegisterDto {

    @JsonProperty("register_emailId")
    @NotBlank(message = "Email should not be blank")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Please enter valid email")
    private String emailId;

    @JsonProperty("register_password")
    @Pattern(regexp = "^.{2,}$",message = "Password should be minimum of 2 Chars")
    private String password;

    @JsonProperty("register_name")
    @NotBlank(message = "Username should not be blank")
    @NotEmpty(message = "Username should not be empty")
    @Pattern(regexp = "^.{2,}$",message = "Username should be minimum of 2 Chars")
    private String userName;
}
