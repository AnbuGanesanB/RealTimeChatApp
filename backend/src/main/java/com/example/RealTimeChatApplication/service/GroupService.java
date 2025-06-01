package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.mapper.UserDetailMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.AddGroupDto;
import com.example.RealTimeChatApplication.model.group.EditGroupDto;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.group.GroupUpdateDto;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.message.MessageType;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.GroupRepo;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepo groupRepo;
    private final UserService userService;
    private final ContactService contactService;
    private final ContactDetailsMapper contactDetailsMapper;
    private final GroupMembershipService groupMembershipService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserDetailMapper userDetailMapper;
    @Autowired
    private TaskScheduler taskScheduler;


    @Transactional
    public void createNewGroup(AddGroupDto addGroupDto){
        Group group = new Group();
        group.setType(RecipientType.GROUP);
        group.setGroupName(addGroupDto.getName());

        for(int newMemberId:addGroupDto.getMembers()){
            User newMember = userService.getUserById(newMemberId);
            groupMembershipService.createNewGroupMembership(newMember,group);
            Contact newlySavedGroupContact = contactService.addNewGroupContactManually(newMember,group);
            contactService.sendNewContactMessageToUser(newMember, newlySavedGroupContact);
            //----------------------------------
            //Send UPDATE-MESSAGE 'USER' created the Group
        }
        Group newlySavedGroup = groupRepo.save(group);
        // Send updated Contacts for concern id's
        //taskScheduler.schedule(()->sendGroupMembersUpdate(newlySavedGroup.getId()), Instant.now().plusSeconds(2));
        sendUpdatedContactToRecipients(newlySavedGroup.getId());
    }

    public List<User> getActiveMembers(Group group){
        return group.getMembers().stream()
                .filter(m -> m.getMembershipStatus() == MembershipStatus.ACTIVE)
                .map(GroupMembership::getGroupMemberId).toList();
    }



    public void sendGroupMembersUpdate(int groupId){

        Group group = getGroupById(groupId);
        GroupUpdateDto groupUpdateDto = new GroupUpdateDto();

        groupUpdateDto.setType(MessageType.GROUP_MEMBER_ADD.name());
        groupUpdateDto.setGroupId(group.getId());
        groupUpdateDto.setUpdatedMemberDetails(getActiveMembers(group)
                .stream()
                .map(userDetailMapper::getUserDetails)
                .toList());

        String dest = "/group/"+group.getId().toString()+"/queue/updates";
        simpMessagingTemplate.convertAndSend(dest,groupUpdateDto);
    }

    public void sendUpdatedContactToRecipients(int groupId){
        System.out.println("In Sending Updated contact");
        Group group = getGroupById(groupId);
        //Set<Contact> contactOf = group.getContactOf();
        for(Contact contact:group.getContactOf()){
            System.out.println("Sending Updated Contact to: "+contact.getOwner().getUserName());
            contactService.sendUpdatedContactMessageToUser(contact.getOwner(),contact);
        }
    }

    public void addMembers(EditGroupDto editGroupDto){
        //
    }

    public void removeMembers(EditGroupDto editGroupDto){

    }

    public Group getGroupById(int groupId){
        return groupRepo.findById(groupId)
                .orElseThrow(()->new RuntimeException("Group not found"));
    }
}
