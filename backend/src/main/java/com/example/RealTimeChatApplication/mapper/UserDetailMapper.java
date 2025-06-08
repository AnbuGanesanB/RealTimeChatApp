package com.example.RealTimeChatApplication.mapper;

import com.example.RealTimeChatApplication.model.user.LoggedUserInfoDto;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.model.user.UserDetailsDto;
import com.example.RealTimeChatApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class UserDetailMapper {

    //private final UserService userService;

    public UserDetailsDto getUserDetails(User user){
        UserDetailsDto userDetailsDto = new UserDetailsDto();

        userDetailsDto.setId(user.getId());
        userDetailsDto.setName(user.getUsername());
        userDetailsDto.setEmailId(user.getEmailId());
        userDetailsDto.setRecipientType(user.getType().toString());

        userDetailsDto.setInitials(user.getInitials());
        userDetailsDto.setDpAvailable(user.isDpAvailable());
        userDetailsDto.setDpPath(user.getDpPath());
        userDetailsDto.setOnlineStatus(user.getOnlineStatus().name());
        return userDetailsDto;
    }

    public LoggedUserInfoDto getLoggedUserInfo(User user){
        LoggedUserInfoDto loggedUserInfoDto = new LoggedUserInfoDto();

        loggedUserInfoDto.setUserId(user.getId());
        loggedUserInfoDto.setName(user.getUsername());
        loggedUserInfoDto.setEmailId(user.getEmailId());
        loggedUserInfoDto.setInitials(user.getInitials());
        loggedUserInfoDto.setAboutMe(user.getAboutMe());
        loggedUserInfoDto.setDpAvailable(user.isDpAvailable());
        loggedUserInfoDto.setDpPath(user.getDpPath());
        loggedUserInfoDto.setOnlineStatus(user.getOnlineStatus().name());
        return loggedUserInfoDto;
    }


}
