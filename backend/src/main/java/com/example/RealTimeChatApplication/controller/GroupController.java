package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.model.group.AddGroupDto;
import com.example.RealTimeChatApplication.model.group.EditGroupDto;
import com.example.RealTimeChatApplication.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/creategroup")
    @ResponseBody
    public void createNewGroup(@RequestBody AddGroupDto addGroupDto){
        for(int id:addGroupDto.getMembers()){
            System.out.println("Member id: "+id);
        }
        groupService.createNewGroup(addGroupDto);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/editgroup")
    @ResponseBody
    public void editGroup(@RequestBody EditGroupDto editGroupDto){

    }
}
