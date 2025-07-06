package com.example.RealTimeChatApplication.mapper;

import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.GroupMembershipRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactDetailsMapper {

    private final UserDetailMapper userDetailMapper;
    private final GroupMembershipRepo groupMembershipRepo;

    public ContactDetailsDto retrieveContactDetails(Contact contact) {
        ContactDetailsDto contactDetailsDto = new ContactDetailsDto();

        contactDetailsDto.setId(contact.getId());
        contactDetailsDto.setOwner(contact.getOwner().getUsername());
        contactDetailsDto.setOwnerId(contact.getOwner().getId());

        contactDetailsDto.setNickName(contact.getNickName());
        contactDetailsDto.setType(contact.getType().toString());
        contactDetailsDto.setAddedDate(contact.getAddedDate());
        contactDetailsDto.setLastVisitedAt(contact.getLastVisitedAt());
        contactDetailsDto.setUnreadMessages(contact.getUnreadMessages());

        if(contact.getContactPerson() != null){
            // ------- USER
            User contactPerson = contact.getContactPerson();

            contactDetailsDto.setContactPersonOrGroupName(contactPerson.getUsername());
            contactDetailsDto.setContactPersonOrGroupId(contactPerson.getId());
            contactDetailsDto.setGroupMemberDetails(null);

            contactDetailsDto.setDpAvailable(contactPerson.isDpAvailable());
            contactDetailsDto.setDpPath(contactPerson.getDpPath());
            contactDetailsDto.setAboutMe(contactPerson.getAboutMe());
            contactDetailsDto.setOnlineStatus(contactPerson.getOnlineStatus().name());
            contactDetailsDto.setInitials(contactPerson.getInitials());
            contactDetailsDto.setRemovedMemberIds(null);
        }else{
            // ------- GROUP
            Group group = contact.getContactGroup();

            contactDetailsDto.setContactPersonOrGroupName(group.getGroupName());
            contactDetailsDto.setContactPersonOrGroupId(group.getId());
            contactDetailsDto.setGroupMemberDetails(group.getMembers().stream()
                    //.filter(m -> m.getMembershipStatus() == MembershipStatus.ACTIVE)
                    .map(GroupMembership::getGroupMemberId)
                    .map(userDetailMapper::getUserDetails)
                    .toList());

            contactDetailsDto.setDpAvailable(group.isDpAvailable());
            contactDetailsDto.setDpPath(group.getDpPath());
            contactDetailsDto.setAboutMe("");
            contactDetailsDto.setOnlineStatus("");
            contactDetailsDto.setInitials(group.getInitials());
            contactDetailsDto.setRemovedMemberIds(groupMembershipRepo.findInactiveMembers(group)
                    .stream()
                    .map(GroupMembership::getGroupMemberId)
                    .map(User::getId)
                    .collect(Collectors.toList()));
        }

        if(contact.getLastMessage() != null){
            Message lastMessage = contact.getLastMessage();
            contactDetailsDto.setLastMessageFromUser(lastMessage.getSender().getUsername());
            contactDetailsDto.setLastMessageSenderId(lastMessage.getSender().getId());

            String lastMessageContent = null;
            switch (lastMessage.getMessageType()) {
                case GROUP_CREATION -> {
                    lastMessageContent = "Created Group";
                }
                case GROUP_MEMBER_ADD -> {
                    lastMessageContent = "Added Members";
                }
                case GROUP_MEMBER_REMOVED -> {
                    lastMessageContent = "Removed Members";
                }
                case GROUP_NAME_CHANGE -> {
                    lastMessageContent = "Changed Group name";
                }
                case GROUP_DP_ADDED -> {
                    lastMessageContent = "Added DP";
                }
                case GROUP_DP_CHANGED -> {
                    lastMessageContent = "Changed DP";
                }
                case GROUP_DP_REMOVED -> {
                    lastMessageContent = "Removed DP";
                }
                case USER_LEFT_GROUP -> {
                    lastMessageContent = "Left the group";
                }
                // Default - Text Message
                case TEXT_MESSAGE -> {
                    lastMessageContent = lastMessage.isContainsFile() ? "Sent Files" : lastMessage.getContent();
                }
                /*default -> {
                    lastMessageContent = lastMessage.getContent();
                }*/
            }
            contactDetailsDto.setLastMessageContent(lastMessageContent);
        }

        return contactDetailsDto;
    }
}
