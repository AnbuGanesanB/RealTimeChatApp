package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.configuration.ProfilePicConfig;
import com.example.RealTimeChatApplication.exception.ContactException;
import com.example.RealTimeChatApplication.exception.GroupException;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.AddGroupDto;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.message.MessageType;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.GroupMembershipRepo;
import com.example.RealTimeChatApplication.repositories.GroupRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepo groupRepo;
    private final UserService userService;
    private final ContactService contactService;
    private final GroupMembershipService groupMembershipService;
    private final FileService fileService;
    private final MessageService messageService;
    private final GroupMembershipRepo groupMembershipRepo;
    private final ProfilePicConfig profilePicConfig;

    @Transactional
    public void createNewGroup(AddGroupDto addGroupDto){
        if(addGroupDto.getName().trim().length()<2) throw new GroupException.ShortGroupNameException("Group Name should be minimum 2 chars");

        Group group = new Group();
        group.setType(RecipientType.GROUP);
        group.setGroupName(addGroupDto.getName());
        group = groupRepo.save(group);

        User creator = userService.getUserById(addGroupDto.getCreatorId());

        for(int newMemberId:addGroupDto.getMembers()){
            User newMember = userService.getUserById(newMemberId);
            groupMembershipService.createNewGroupMembership(newMember,group);
            Contact newlySavedGroupContact = contactService.addNewGroupContactManually(newMember,group);
            contactService.sendNewContactMessageToUser(newMember, newlySavedGroupContact);
        }
        Message savedMessage = messageService.processGroupCreationMessage(creator,group,addGroupDto.getMembers());
        Contact creator_grpContact = contactService.getContactByOwnerAndContactGroup(creator,group);
        contactService.processContactsForIncomingMessage(creator_grpContact.getId(),savedMessage);
        contactService.sendUpdatedContactToAllRecipients(creator_grpContact.getId());
    }

    /**
     * Input is New/current applicable member list
     * Based on difference from previous list, if current user not in existing user list -> user must be added
     */
    public void processMemberAddition(int contactId, List<Integer> userIds){
        Contact contact = contactService.getContactById(contactId);
        Group group = contact.getContactGroup();
        User initiator = contact.getOwner();

        //add Members
        addMembers(group,userIds);
        //process Message
        Message savedMessage = messageService.processMemberAdditionMessage(initiator,group,userIds);
        //Increment the message counter
        contactService.processContactsForIncomingMessage(contactId,savedMessage);
        //fire updated contact which have updated added members - so that FE can subscribe
        contactService.sendUpdatedContactToAllRecipients(contactId);
        //Broadcast message by WS
        messageService.distributeMessage(savedMessage,contactId);
    }


    public void addMembers(Group group,List<Integer> userIds){
        for(int id:userIds){
            User user = userService.getUserById(id);
            try{
                // Removed User may be added again
                // Contact exists. Only GroupMembership status needs to set ACTIVE again
                GroupMembership oldMembership = groupMembershipService.findGroupMembership(group,user);
                oldMembership.setMembershipStatus(MembershipStatus.ACTIVE);
                oldMembership.setRemovedAt(null);
                groupMembershipRepo.save(oldMembership);
            }catch (NoSuchElementException e){
                // User may be whole-new
                // New Contact and GroupMembership needs to be created
                groupMembershipService.createNewGroupMembership(user,group);
                System.out.println("Adding new Member:"+user.getUsername());
                Contact newlySavedGroupContact = contactService.addNewGroupContactManually(user,group);
            }
        }
    }

    /**
     * Input is New/current applicable member list
     * Based on difference from previous list, if existing user not in new list -> user must be removed
     */
    public void processMemberRemoval(int contactId, List<Integer> userIds) {
        Contact contact = contactService.getContactById(contactId);
        Group group = contact.getContactGroup();
        User initiator = contact.getOwner();

        Message savedMessage = messageService.processMemberRemovalMessage(initiator,group,userIds);
        messageService.distributeMessage(savedMessage,contactId);
        contactService.processContactsForIncomingMessage(contactId,savedMessage);
        contactService.sendUpdatedContactToAllRecipients(contactId);
        removeMembers(userIds,group);
        contactService.sendUpdatedContactToAllRecipients(contactId);
    }

    private void removeMembers(List<Integer> userIds, Group group){
        for(int id:userIds){
            User user = userService.getUserById(id);
            GroupMembership oldMembership = groupMembershipService.findGroupMembership(group,user);
            oldMembership.setMembershipStatus(MembershipStatus.INACTIVE);
            oldMembership.setRemovedAt(LocalDateTime.now());
            groupMembershipRepo.save(oldMembership);
        }
    }

    public void processGroupDpChange(int contactId, MultipartFile profilePic){

        Contact contact = contactService.getContactById(contactId);
        Group group = contact.getContactGroup();
        User sender = contact.getOwner();

        MessageType messageType = selectGroupDpMessageType(group,profilePic);
        group = handleGroupDpPicture(group,profilePic);
        Message savedMessage = messageService.processAutoGroupDpMessage(sender,group,messageType);
        contactService.processContactsForIncomingMessage(contactId,savedMessage);
        messageService.distributeMessage(savedMessage,contactId);
        contactService.sendUpdatedContactToAllRecipients(contactId);
    }

    public Group handleGroupDpPicture(Group group, MultipartFile profilePic){

        if(profilePic==null){
            group.setDpPath(null);
        }else{
            if (!profilePicConfig.getAllowedTypes().contains(profilePic.getContentType())) {
                throw new GroupException.FileTypeMismatchException("File type must be JPG or PNG only");
            }
            if(profilePic.getSize()>profilePicConfig.getMaxSize().toBytes()){
                throw new GroupException.FileOverSizeException("Uploaded file size must be less than "+profilePicConfig.getMaxSize());
            }
            group = fileService.setGroupPicture(profilePic,group);
        }

        return groupRepo.save(group);
    }

    MessageType selectGroupDpMessageType(Group group, MultipartFile profilePic){
        MessageType messageType = null;
        boolean isHadDpPreviously = group.isDpAvailable();

        if(profilePic==null){
            messageType = MessageType.GROUP_DP_REMOVED;
        }else{
            messageType = isHadDpPreviously ? MessageType.GROUP_DP_CHANGED : MessageType.GROUP_DP_ADDED;
        }

        return messageType;
    }

    private void processGroupNameChange(int contactId, String newGroupName) {
        Contact contact = contactService.getContactById(contactId);
        Group group = contact.getContactGroup();
        User sender = contact.getOwner();

        group = changeGroupName(group,newGroupName);

        Message savedMessage = messageService.processGroupNameChangeMessage(sender,group,newGroupName);
        contactService.processContactsForIncomingMessage(contactId,savedMessage);
        contactService.sendUpdatedContactToAllRecipients(contactId);

        messageService.distributeMessage(savedMessage,contactId);
    }

    public Group changeGroupName(Group group, String newName){
        if(newName.trim().length()<2) throw new GroupException.ShortGroupNameException("Group Name should be minimum 2 chars");
        group.setGroupName(newName);
        return groupRepo.save(group);
    }

    public void processEditGroup(int contactId, MultipartFile profilePic, boolean isDpChanged, String newGroupName, List<Integer> newMemberIds, String newNickName) {

        Contact contact = contactService.getContactById(contactId);
        Group group = contact.getContactGroup();
        User sender = contact.getOwner();
        boolean isUserSelfRemoving = false;

        if(newMemberIds==null) throw new GroupException.MinimumMembersException("Minimum one member is mandatory for group");

        //Modify members
        List<Integer> currentMemberIds = groupMembershipRepo.findActiveMembers(group).stream().map(m->m.getGroupMemberId()).map(u->u.getId()).toList();

        //Remove Users
        List<Integer> currentMinusNew = new ArrayList<>(currentMemberIds);
        currentMinusNew.removeAll(newMemberIds);
        //First Check for User Self Removal & but process this as Last activity
        if(currentMinusNew.contains(sender.getId())){
            //processMemberSelfRemoval();
            isUserSelfRemoving = true;
            System.out.println("Before removing self:"+currentMinusNew);
            currentMinusNew.remove((Object) sender.getId());
            System.out.println("After removing self:"+currentMinusNew);
        }
        if(currentMinusNew.size()>0) processMemberRemoval(contactId,currentMinusNew);

        //Add Users
        List<Integer> newMinusCurrent = new ArrayList<>(newMemberIds);
        newMinusCurrent.removeAll(currentMemberIds);
        if(newMinusCurrent.size()>0) processMemberAddition(contactId,newMinusCurrent);

        //Change GroupDP
        if(isDpChanged) processGroupDpChange(contactId,profilePic);

        //Change GroupName
        if(!group.getGroupName().equals(newGroupName)) processGroupNameChange(contactId, newGroupName);

        //Modify nickname
        if(!contact.getNickName().equals(newNickName)) processChangeNickName(contactId, newNickName);

        if(isUserSelfRemoving) processMemberSelfRemoval(contactId);

    }

    private void processChangeNickName(int contactId, String newNickName) {
        if(newNickName.trim().length()<2) throw new ContactException.ShortContactNameException("Nickname should be minimum 2 characters");

        Contact updatedContact = contactService.updateNickName(contactId,newNickName);
        contactService.sendUpdatedContactMessageToUser(updatedContact.getOwner(),updatedContact);
    }

    private void processMemberSelfRemoval(int contactId) {
        Contact contact = contactService.getContactById(contactId);
        Group group = contact.getContactGroup();
        User initiator = contact.getOwner();

        Message savedMessage = messageService.processMemberSelfRemovalMessage(initiator,group);
        messageService.distributeMessage(savedMessage,contactId);
        contactService.processContactsForIncomingMessage(contactId,savedMessage);
        contactService.sendUpdatedContactToAllRecipients(contactId);
        removeMembers(List.of(initiator.getId()),group);
        contactService.sendUpdatedContactToAllRecipients(contactId);
    }

    public Group getGroupById(int groupId){
        return groupRepo.findById(groupId)
                .orElseThrow(()->new RuntimeException("Group not found"));
    }

}
