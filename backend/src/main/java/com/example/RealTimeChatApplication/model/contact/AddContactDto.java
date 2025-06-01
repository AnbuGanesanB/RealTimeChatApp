package com.example.RealTimeChatApplication.model.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddContactDto {

    private int senderId;
    private int contactPersonId;
}
