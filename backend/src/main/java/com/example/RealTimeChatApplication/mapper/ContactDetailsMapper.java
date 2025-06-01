package com.example.RealTimeChatApplication.mapper;

import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactDetailsMapper {

    private final UserDetailMapper userDetailMapper;

    public ContactDetailsDto retrieveContactDetails(Contact contact) {
        ContactDetailsDto contactDetailsDto = new ContactDetailsDto();

        contactDetailsDto.setId(contact.getId());
        contactDetailsDto.setOwner(contact.getOwner().getUserName());
        contactDetailsDto.setOwnerId(contact.getOwner().getId());

        contactDetailsDto.setNickName(contact.getNickName());
        contactDetailsDto.setType(contact.getType().toString());
        contactDetailsDto.setAddedDate(contact.getAddedDate());
        contactDetailsDto.setLastVisitedAt(contact.getLastVisitedAt());
        contactDetailsDto.setUnreadMessages(contact.getUnreadMessages());

        if(contact.getContactPerson() != null){
            // ------- USER
            User contactPerson = contact.getContactPerson();

            contactDetailsDto.setContactPersonOrGroupName(contactPerson.getUserName());
            contactDetailsDto.setContactPersonOrGroupId(contactPerson.getId());
            contactDetailsDto.setGroupMemberDetails(null);

            contactDetailsDto.setDpAvailable(contactPerson.isDpAvailable());
            contactDetailsDto.setDpPath(contactPerson.getDpPath());
            contactDetailsDto.setAboutMe(contactPerson.getAboutMe());
            contactDetailsDto.setOnlineStatus(contactPerson.getOnlineStatus().name());
            contactDetailsDto.setInitials(contactPerson.getInitials());
        }else{
            // ------- GROUP
            Group group = contact.getContactGroup();

            contactDetailsDto.setContactPersonOrGroupName(group.getGroupName());
            contactDetailsDto.setContactPersonOrGroupId(group.getId());
            contactDetailsDto.setGroupMemberDetails(group.getMembers().stream()
                    .filter(m -> m.getMembershipStatus() == MembershipStatus.ACTIVE)
                    .map(GroupMembership::getGroupMemberId)
                    .map(userDetailMapper::getUserDetails)
                    .toList());

            contactDetailsDto.setDpAvailable(group.isDpAvailable());
            contactDetailsDto.setDpPath(group.getDpPath());
            contactDetailsDto.setAboutMe("");
            contactDetailsDto.setOnlineStatus("");
            contactDetailsDto.setInitials(group.getInitials());
        }

        return contactDetailsDto;
    }
}
