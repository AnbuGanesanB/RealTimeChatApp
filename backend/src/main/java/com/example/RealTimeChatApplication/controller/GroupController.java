package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.model.group.AddGroupDto;
import com.example.RealTimeChatApplication.model.group.EditGroupDto;
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
                          @RequestParam("newMemberIds") List<Integer> newMemberIds,
                          @RequestParam("newNickName") String newNickName) {
        System.out.println("In Edit Group method    .........");
        System.out.println("isDpChanged "+isDpChanged);
        System.out.println("contactId "+contactId);
        System.out.println("newGroupName "+newGroupName);
        System.out.println("newMemberIds "+newMemberIds.size());

        groupService.processEditGroup(contactId, profilePic, isDpChanged, newGroupName, newMemberIds, newNickName);

    }
}
