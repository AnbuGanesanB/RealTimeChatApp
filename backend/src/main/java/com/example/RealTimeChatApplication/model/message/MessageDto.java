package com.example.RealTimeChatApplication.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {


    private String recipientId;

    private String content;
    private int contactId;
}
