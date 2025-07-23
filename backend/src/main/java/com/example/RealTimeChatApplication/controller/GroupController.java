package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.model.group.AddGroupDto;
import com.example.RealTimeChatApplication.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createGroup")
    public void createNewGroup(@RequestBody AddGroupDto addGroupDto){
        groupService.createNewGroup(addGroupDto);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/editGroup")
    public void editGroup(@RequestParam("contactId") int contactId,
                          @RequestParam(value = "profilePic",required = false) MultipartFile profilePic,
                          @RequestParam("isDpChanged") boolean isDpChanged,
                          @RequestParam("newGroupName") String newGroupName,
                          @RequestParam(name = "newMemberIds" , required = false) List<Integer> newMemberIds,
                          @RequestParam("newNickName") String newNickName) {
        groupService.processEditGroup(contactId, profilePic, isDpChanged, newGroupName, newMemberIds, newNickName);
    }
}
