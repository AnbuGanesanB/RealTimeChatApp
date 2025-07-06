package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.mapper.MessageMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.message.MessageType;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.GroupMembershipRepo;
import com.example.RealTimeChatApplication.repositories.MessageRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final ContactService contactService;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;
    private final FileService fileService;
    private final GroupMembershipRepo groupMembershipRepo;

    @Transactional
    public void processIncomingMessage(int contactId, String content, List<MultipartFile> files){
        //contactService.processContactsForIncomingMessage(contactId);
        Message message = saveIncomingMessage(contactId, content, files);
        contactService.processContactsForIncomingMessage(contactId,message);
        distributeMessage(message,contactId);
    }

    @Transactional
    private Message saveIncomingMessage(int contactId, String content, List<MultipartFile> files){
        Contact contact = contactService.getContactById(contactId);
        System.out.println("In Message saving method");
        Message message = new Message();
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setMessageType(MessageType.TEXT_MESSAGE);
        message.setSender(contact.getOwner());                        //Contact owner is the sender of the message

        // based on contactId & recipient type - set recipient
        switch (contact.getType()) {
            case USER -> message.setIndRecipient(contact.getContactPerson());
            case GROUP -> message.setGrpRecipient(contact.getContactGroup());
        }

        boolean isFilesPresent = files != null;
        message.setContainsFile(isFilesPresent);
        message = messageRepo.save(message);

        if(isFilesPresent) fileService.processIncomingFiles(files,message);
        return message;
    }

    @Transactional
    public void distributeMessage(Message message, int contactId){
        Contact contact = contactService.getContactById(contactId);

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
                List<Message> firstSet = messageRepo.findBySenderAndIndRecipient(firstPerson,secondPerson);
                List<Message> secondSet = messageRepo.findBySenderAndIndRecipient(secondPerson, firstPerson);

                combinedMessages.addAll(firstSet);
                combinedMessages.addAll(secondSet);
            }else{                                              // user viewing self contact
                List<Message> firstSet = messageRepo.findBySenderAndIndRecipient(firstPerson,secondPerson);
                combinedMessages.addAll(firstSet);
            }

        } else if (contact.getContactGroup() != null) {

            Group group = contact.getContactGroup();
            GroupMembership membership = groupMembershipRepo.findByGroupMemberIdAndGroupId(firstPerson,group);
                                                                    // Active Users get whole chat
            if(membership.getMembershipStatus() == MembershipStatus.ACTIVE){
                combinedMessages.addAll(messageRepo.findByGrpRecipient(group));
            }else {                                                 // InActive Users get restricted chats
                combinedMessages.addAll(messageRepo.findByGrpRecipientAndTimestampBefore(group, membership.getRemovedAt()));
            }
        }

        contactService.updateLastSeen(contact);
        contactService.updateUnreadMessages(contact);

        return combinedMessages.stream()
                .map(messageMapper::processOutMessage)
                .sorted(Comparator.comparing(OutMessageDto::getTimestamp))
                .collect(Collectors.toList());
    }

    public Message processGroupCreationMessage(User initiator, Group group, List<Integer> newMemberIds){
        Set<User> addedUsers = new HashSet<>(newMemberIds.size());
        System.out.println("List size is: "+newMemberIds.size());
        for(int id:newMemberIds) {
            if(id==initiator.getId()) continue;     // Not adding creator to Linked User's list
            System.out.println("Adding "+id+" to the group");
            addedUsers.add(userService.getUserById(id));
        }
        System.out.println("Added users: "+addedUsers);
        Message message = new Message();
        message.setMessageType(MessageType.GROUP_CREATION);
        message.setSender(initiator);
        message.setGrpRecipient(group);
        message.setTimestamp(LocalDateTime.now());
        message.setContent(group.getGroupName());
        message.setLinkedUsers(addedUsers);
        return messageRepo.save(message);
    }

    public Message processGroupNameChangeMessage(User initiator, Group group, String newGroupName) {
        Message message = new Message();
        message.setMessageType(MessageType.GROUP_NAME_CHANGE);
        message.setSender(initiator);
        message.setGrpRecipient(group);
        message.setTimestamp(LocalDateTime.now());
        message.setContent(newGroupName);
        return messageRepo.save(message);
    }

    public Message processAutoGroupDpMessage(User initiator, Group group, MessageType messageType) {
        Message message = new Message();
        message.setMessageType(messageType);
        message.setSender(initiator);
        message.setGrpRecipient(group);
        message.setTimestamp(LocalDateTime.now());
        return messageRepo.save(message);
    }

    public Message processMemberRemovalMessage(User initiator, Group group, List<Integer> userIds){
        Set<User> removedUsers = new HashSet<>(userIds.size());
        for(int id:userIds) {
            removedUsers.add(userService.getUserById(id));
        }
        Message message = new Message();
        message.setMessageType(MessageType.GROUP_MEMBER_REMOVED);
        message.setTimestamp(LocalDateTime.now());
        message.setSender(initiator);
        message.setGrpRecipient(group);
        message.setLinkedUsers(removedUsers);
        return messageRepo.save(message);
    }

    public Message processMemberSelfRemovalMessage(User initiator, Group group){
        Message message = new Message();
        message.setMessageType(MessageType.USER_LEFT_GROUP);
        message.setTimestamp(LocalDateTime.now());
        message.setSender(initiator);
        message.setGrpRecipient(group);
        return messageRepo.save(message);
    }

    public Message processMemberAdditionMessage(User initiator, Group group, List<Integer> userIds) {
        Set<User> addedUsers = new HashSet<>(userIds.size());
        for(int id:userIds) {
            addedUsers.add(userService.getUserById(id));
        }
        Message message = new Message();
        message.setMessageType(MessageType.GROUP_MEMBER_ADD);
        message.setSender(initiator);
        message.setGrpRecipient(group);
        message.setTimestamp(LocalDateTime.now());
        message.setLinkedUsers(addedUsers);
        return messageRepo.save(message);
    }

    /**
     * if: Recipient is Person - Trigger updated_Reverse Contact towards Recipients
     * message A-->B, Notify B; Update B's contact where A is Recipient
     * if: Recipient is Group - Other than original sender, send updated Group contact for each member
     */
    public void sendUpdatedContactToRecipients(int contactId){
        contactService.sendUpdatedContactToAllRecipients(contactId);
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

}
