package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.mapper.MessageMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.message.MessageDto;
import com.example.RealTimeChatApplication.model.message.MessageType;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.MessageRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final ContactService contactService;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ContactDetailsMapper contactDetailsMapper;
    private final GroupService groupService;
    private final FileService fileService;

    @Transactional
    public void processIncomingMessage(int contactId, String content, List<MultipartFile> files){
        Contact contact = contactService.getContactById(contactId);
        Message message = processMetaAndSaveMessage(contact, content, files);
        distributeMessage(message,contact);
    }

    @Transactional
    private Message processMetaAndSaveMessage(Contact contact, String content, List<MultipartFile> files){
        Message message = new Message();
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setMessageType(MessageType.TEXT_MESSAGE);

        User contactOwner = contact.getOwner();
        message.setSender(contactOwner);                        //Contact owner is the sender of the message

        RecipientType recipientType = contact.getType();        // based on contactId & recipient type - set recipient
        if(recipientType == RecipientType.USER){

            User contactPerson = contact.getContactPerson();
            message.setIndRecipient(contactPerson);

            if(contactOwner != contactPerson){
                // Reverse-Contact not exists
                if(!contactService.existsContact(contactPerson, contactOwner)) {

                    // Contact-person is the owner and vice-versa - to create new reverse contact
                    // & Contact Update - sent to real time update
                    Contact newlySavedReverseContact = contactService.addNewUserContactManually(contactPerson, contactOwner);
                    newlySavedReverseContact = contactService.incrementUnreadMessages(newlySavedReverseContact);
                    contactService.sendNewContactMessageToUser(contactPerson,newlySavedReverseContact);
                }else{
                    // Getting Reverse contact to save unread message
                    Contact reverseContact = contactService.getContactByOwnerAndContactPerson(contactPerson,contactOwner);
                    contactService.incrementUnreadMessages(reverseContact);
                }
            }

        } else if (recipientType == RecipientType.GROUP) {
            Group contactGroup = contact.getContactGroup();
            message.setGrpRecipient(contactGroup);

            List<User> groupMembers = groupService.getActiveMembers(contactGroup);
            for(User member: groupMembers){
                if(member.equals(contactOwner)) continue;
                Contact user_GroupContact = contactService.getContactByOwnerAndContactGroup(member,contactGroup);
                contactService.incrementUnreadMessages(user_GroupContact);
            }
        }

        boolean isFilesPresent = files != null;
        message.setContainsFile(isFilesPresent);
        message = messageRepo.save(message);

        if(isFilesPresent) fileService.processIncomingFiles(files,message);
        contactService.updateLastSeen(contact);
        return message;
    }

    @Transactional
    private void distributeMessage(Message message, Contact contact){

        RecipientType recipientType = contact.getType();
        OutMessageDto outMessageDto = messageMapper.processOutMessage(message);

        if(recipientType == RecipientType.USER){
            if(contact.getContactPerson() == contact.getOwner()){
                sendIndividualTextMessage(contact.getContactPerson(), outMessageDto);
            }else {
                sendIndividualTextMessage(contact.getContactPerson(), outMessageDto);
                sendIndividualTextMessage(contact.getOwner(), outMessageDto);
            }
        } else if (recipientType == RecipientType.GROUP) {
            sendGroupTextMessage(contact.getContactGroup(), outMessageDto);
        }
    }


    public List<OutMessageDto> getChatMessages(int contactId){

        Contact contact = contactService.getContactById(contactId);
        User firstPerson = contact.getOwner();
        List<Message> combinedMessages = new ArrayList<>();

        if(contact.getContactPerson() != null){
            User secondPerson = contact.getContactPerson();

            if(firstPerson != secondPerson){                    // User NOT viewing self contact
                Set<Message> firstSet = messageRepo.findBySenderAndIndRecipient(firstPerson,secondPerson);
                Set<Message> secondSet = messageRepo.findBySenderAndIndRecipient(secondPerson, firstPerson);

                combinedMessages.addAll(firstSet);
                combinedMessages.addAll(secondSet);
            }else{                                              // user viewing self contact
                Set<Message> firstSet = messageRepo.findBySenderAndIndRecipient(firstPerson,secondPerson);
                combinedMessages.addAll(firstSet);
            }

        } else if (contact.getContactGroup() != null) {

            Set<GroupMembership> groupMemberships = contact.getContactGroup().getMembers();

            for (GroupMembership membership : groupMemberships) {
                User member = membership.getGroupMemberId();
                Group group = membership.getGroupId();

                combinedMessages.addAll(messageRepo.findBySenderAndGrpRecipient(member, group));
            }
        }

        contactService.updateLastSeen(contact);
        contactService.updateUnreadMessages(contact);

        return combinedMessages.stream()
                .map(messageMapper::processOutMessage)
                .sorted(Comparator.comparing(OutMessageDto::getTimestamp))
                .collect(Collectors.toList());
    }

    private void sendIndividualTextMessage(User user, OutMessageDto outMessageDto){
        String dest = "/user/"+user.getId().toString()+"/queue/messages";
        System.out.println("Dest. in user message service: "+dest);
        simpMessagingTemplate.convertAndSend(dest,outMessageDto);
    }

    private void sendGroupTextMessage(Group group, OutMessageDto outMessageDto){
        String dest = "/group/"+group.getId().toString()+"/queue/messages";
        System.out.println("Dest. in group message service: "+dest);
        simpMessagingTemplate.convertAndSend(dest,outMessageDto);
    }

    /**
     * if: Recipient is Person - Trigger updated_Reverse Contact towards Recipients
     * message A-->B, Notify B; Update B's contact where A is Recipient
     * if: Recipient is Group - Other than original sender, send updated Group contact for each member
     */
    public void notifyRecipientsAboutMessage(int contactId){
        Contact contact = contactService.getContactById(contactId);
        if(contact.getType()==RecipientType.USER){
            Contact reverseContact = contactService.getContactByOwnerAndContactPerson(contact.getContactPerson(), contact.getOwner());
            contactService.sendUpdatedContactMessageToUser(reverseContact.getOwner(),reverseContact);
        } else if (contact.getType()==RecipientType.GROUP) {
            Group contactGroup = contact.getContactGroup();
            List<User> groupMembers = groupService.getActiveMembers(contactGroup);
            for(User member: groupMembers){
                Contact user_GroupContact = contactService.getContactByOwnerAndContactGroup(member,contactGroup);
                if(user_GroupContact.equals(contact)) continue;
                contactService.sendUpdatedContactMessageToUser(member,user_GroupContact);
            }
        }

    }
}
