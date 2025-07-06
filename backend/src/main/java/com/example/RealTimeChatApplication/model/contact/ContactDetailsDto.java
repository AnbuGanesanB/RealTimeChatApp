package com.example.RealTimeChatApplication.model.contact;

import com.example.RealTimeChatApplication.model.user.OnlineStatus;
import com.example.RealTimeChatApplication.model.user.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailsDto {


    private Integer id;
    private String owner;                               // Name of the **owner** of the contact
    private int ownerId;                                // Tracks the accurate ID of owner from User table
    private String contactPersonOrGroupName;            // Name of the **contacted person** or **group**
    private int contactPersonOrGroupId;                 // Tracks the accurate ID from respective User/Group tables
    private String nickName;
    private LocalDate addedDate;
    private LocalDateTime lastVisitedAt;
    private String type;
    private int unreadMessages;
    private List<UserDetailsDto> groupMemberDetails;    // valid only for group - else null
    private List<Integer> removedMemberIds;
    private String dpPath;
    private boolean isDpAvailable;
    private String aboutMe;
    private String onlineStatus;
    private String initials;

    private String lastMessageFromUser;
    private String lastMessageContent;
    private int lastMessageSenderId;

}
