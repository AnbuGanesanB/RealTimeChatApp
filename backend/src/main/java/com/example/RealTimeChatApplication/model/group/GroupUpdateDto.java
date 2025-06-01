package com.example.RealTimeChatApplication.model.group;

import com.example.RealTimeChatApplication.model.user.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateDto {
    private int groupId;
    private List<UserDetailsDto> updatedMemberDetails;
    private String type;
}
