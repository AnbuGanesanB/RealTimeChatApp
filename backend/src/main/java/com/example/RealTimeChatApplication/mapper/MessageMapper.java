package com.example.RealTimeChatApplication.mapper;

import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final OutFileMapper outFileMapper;

    public OutMessageDto processOutMessage(Message message){
        OutMessageDto outMessageDto = new OutMessageDto();

        outMessageDto.setId(message.getId());
        outMessageDto.setContent(message.getContent());
        outMessageDto.setSender(message.getSender().getUserName());
        outMessageDto.setSenderId(message.getSender().getId());
        outMessageDto.setMessageType(message.getMessageType().name());
        outMessageDto.setTimestamp(message.getTimestamp());

        if(message.getIndRecipient() != null){                  // If Ind-Recipient is true - GrpRecipient is null and Vice-versa

            outMessageDto.setIndRecipient(message.getIndRecipient().getUserName());
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
        return outMessageDto;
    }
}
