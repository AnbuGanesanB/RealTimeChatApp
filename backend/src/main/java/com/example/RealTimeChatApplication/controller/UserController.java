package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.mapper.UserDetailMapper;
import com.example.RealTimeChatApplication.model.contact.AddContactDto;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.group.AddGroupDto;
import com.example.RealTimeChatApplication.model.group.EditGroupDto;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import com.example.RealTimeChatApplication.model.user.*;
import com.example.RealTimeChatApplication.service.ContactService;
import com.example.RealTimeChatApplication.service.GroupService;
import com.example.RealTimeChatApplication.service.MessageService;
import com.example.RealTimeChatApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDetailMapper userDetailMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void registerNewUser(@RequestBody RegisterDto registerDto){
        userService.createNewUser(registerDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> loginjwt(@RequestBody LoginDto loginDto){
        ResponseEntity<Map<String, String>> responseEntity = userService.authenticateUser(loginDto);
        return responseEntity;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/signout")
    public void logout(@RequestBody Map<String, Integer> loggedUserDetails){
        int loggedUserId = loggedUserDetails.get("loginUserId");
        User loggedUser = userService.getUserById(loggedUserId);
        userService.changeOnlineStatus(loggedUser,OnlineStatus.OFFLINE);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/profileupdate")
    public void sendProfileUpdate(@RequestBody Map<String, Integer> updatedUserDetails){
        int updatedUserId = updatedUserDetails.get("loginUserId");
        User updatedUser = userService.getUserById(updatedUserId);
        CompletableFuture.runAsync(()->{
            userService.sendUpdatedProfileDetails(updatedUser);
        });
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/status")
    @ResponseBody
    public LoggedUserInfoDto changeStatus(@RequestBody Map<String, Integer> updatedUserDetails){
        int updatedUserId = updatedUserDetails.get("loggedUserId");
        User updatedUser = userService.getUserById(updatedUserId);

        int statusIndex = updatedUserDetails.get("statusIndex");
        updatedUser = userService.changeOnlineStatus(updatedUser,statusIndex);

        return userDetailMapper.getLoggedUserInfo(updatedUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/editDisplayProfile")
    @ResponseBody
    public LoggedUserInfoDto editDisplayProfile(@RequestParam(value = "profilePic",required = false) MultipartFile profilePic,
                                                @RequestParam("isDpChanged") boolean isDpChanged,
                                                @RequestParam(value = "aboutMe",required = false) String aboutMe,
                                                @RequestParam("name") String name,
                                                @RequestParam("userId") int userId){
        User loggedUser = userService.getUserById(userId);
        User updatedUser = userService.updateDisplayProfile(loggedUser, name, aboutMe, isDpChanged, profilePic);
        return userDetailMapper.getLoggedUserInfo(updatedUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/fetchUser")
    @ResponseBody
    public LoggedUserInfoDto fetchCurrentUser(){
        User loggedUser = userService.getCurrentUser();
        System.out.println("From Fetch User:");
        System.out.println(loggedUser.getUsername());
        return userDetailMapper.getLoggedUserInfo(loggedUser);
    }

}
