package com.example.RealTimeChatApplication.model.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditGroupDto {

    private int groupId;
    private int senderId;
    private String name;
    private Set<Integer> members;
}
