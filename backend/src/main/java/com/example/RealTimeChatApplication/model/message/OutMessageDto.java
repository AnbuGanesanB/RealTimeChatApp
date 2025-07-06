package com.example.RealTimeChatApplication.model.message;

import com.example.RealTimeChatApplication.model.files.OutFileDto;
import com.example.RealTimeChatApplication.model.user.UserDetailsDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutMessageDto {

    private int id;
    private String sender;
    private int senderId;
    private String content;
    private LocalDateTime timestamp;
    private String indRecipient;
    private int indRecipientId;
    private String grpRecipient;
    private int grpRecipientId;
    private String messageType;
    private List<OutFileDto> files;
    private List<UserDetailsDto> linkedUsers;
}
