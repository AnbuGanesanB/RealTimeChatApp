package com.example.RealTimeChatApplication.mapper;

import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final OutFileMapper outFileMapper;
    private final UserDetailMapper userDetailMapper;

    public OutMessageDto processOutMessage(Message message){
        OutMessageDto outMessageDto = new OutMessageDto();

        outMessageDto.setId(message.getId());
        outMessageDto.setContent(message.getContent());
        outMessageDto.setSender(message.getSender().getUsername());
        outMessageDto.setSenderId(message.getSender().getId());
        outMessageDto.setMessageType(message.getMessageType().name());
        outMessageDto.setTimestamp(message.getTimestamp());

        if(message.getIndRecipient() != null){                  // If Ind-Recipient is true - GrpRecipient is null and Vice-versa

            outMessageDto.setIndRecipient(message.getIndRecipient().getUsername());
            outMessageDto.setIndRecipientId(message.getIndRecipient().getId());

            outMessageDto.setGrpRecipient("");
            outMessageDto.setGrpRecipientId(0);
        }else{
            outMessageDto.setIndRecipient("");
            outMessageDto.setIndRecipientId(0);

            outMessageDto.setGrpRecipient(message.getGrpRecipient().getGroupName());
            outMessageDto.setGrpRecipientId(message.getGrpRecipient().getId());
        }

        outMessageDto.setFiles(
                message.isContainsFile()
                        ? message.getSharedFiles().stream().map(outFileMapper::getFileDetails).toList()
                        : null);

        switch (message.getMessageType()) {
            case GROUP_MEMBER_REMOVED, GROUP_MEMBER_ADD, GROUP_CREATION -> {
                outMessageDto.setLinkedUsers(message.getLinkedUsers().stream().map(userDetailMapper::getUserDetails).collect(Collectors.toList()));
            }
            default -> {
                outMessageDto.setLinkedUsers(null);
            }
        }
        return outMessageDto;
    }
}
