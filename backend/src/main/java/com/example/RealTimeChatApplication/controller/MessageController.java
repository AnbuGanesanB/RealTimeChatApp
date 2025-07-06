package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.message.MessageDto;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import com.example.RealTimeChatApplication.service.ContactService;
import com.example.RealTimeChatApplication.service.GroupService;
import com.example.RealTimeChatApplication.service.MessageService;
import com.example.RealTimeChatApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Controller
public class MessageController {


    private final MessageService messageService;
    private final ContactService contactService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/uploadMessage")
    public void uploadFiles(@RequestParam(value = "files",required = false) List<MultipartFile> files,
                            @RequestParam("contactId") Integer contactId,
                            @RequestParam("content") String content) {
        messageService.processIncomingMessage(contactId, content, files);
        messageService.sendUpdatedContactToRecipients(contactId);

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chats")
    @ResponseBody
    public List<OutMessageDto> getAllChats(@RequestBody Map<String, Integer> ids) {
        int oldContactId = ids.get("oldContactId");
        int newContactId = ids.get("newContactId");

        CompletableFuture.runAsync(() -> {
            if(oldContactId==0){
                System.out.println("In Async method for now");
            }else{
                System.out.println("Let's process old contact updation");
                Contact updatedPreviousContact = contactService.updatePreviousContactState(oldContactId);
                contactService.sendUpdatedContactMessageToUser(updatedPreviousContact.getOwner(),updatedPreviousContact);
            }
        });
        return messageService.getChatMessages(newContactId);
    }

}
