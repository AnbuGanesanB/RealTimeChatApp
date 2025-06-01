package com.example.RealTimeChatApplication.model.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddGroupDto {

    //private int creatorId;
    private String name;
    private List<Integer> members;
}
