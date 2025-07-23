package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.exception.ContactException;
import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.mapper.UserDetailMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.model.user.UserDetailsDto;
import com.example.RealTimeChatApplication.repositories.ContactRepo;
import com.example.RealTimeChatApplication.repositories.GroupMembershipRepo;
import com.example.RealTimeChatApplication.repositories.GroupRepo;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepo contactRepo;
    private final ContactDetailsMapper contactDetailsMapper;
    private final UserRepo userRepo;
    private final GroupRepo groupRepo;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GroupMembershipRepo groupMembershipRepo;
    private final UserDetailMapper userDetailMapper;

    public Contact getContactById(int contactId){
        return contactRepo.findById(contactId)
                .orElseThrow(()->new RuntimeException("Contact not found"));
    }

    public boolean existsContact(User owner, User contactPerson) {
        return contactRepo.existsByOwnerAndContactPerson(owner, contactPerson);
    }

    @Transactional
    public void updateLastSeen(Contact contact) {
        contact.setLastVisitedAt(LocalDateTime.now());
        contactRepo.save(contact);
    }

    public Contact getContactByOwnerAndContactPerson(User owner, User contactPerson){
        return contactRepo.findByOwnerAndContactPerson(owner,contactPerson);
    }

    public Contact getContactByOwnerAndContactGroup(User owner, Group contactGroup){
        return contactRepo.findByOwnerAndContactGroup(owner,contactGroup)
                .orElseThrow(() -> new NoSuchElementException("Contact not found"));
    }

    public Set<ContactDetailsDto> retrieveAllChatContacts(User loggedUser) {
        return loggedUser.getContacts().stream().map(contactDetailsMapper::retrieveContactDetails).collect(Collectors.toSet());
    }

    @Transactional
    public Contact updateUnreadMessages(Contact contact) {
        contact.setUnreadMessages(0);
        return contactRepo.save(contact);
    }

    public Contact updatePreviousContactState(int contactId){
        Contact contact = getContactById(contactId);
        updateUnreadMessages(contact);
        updateLastSeen(contact);
        return contact;
    }

    public Contact updateNickName(int contactId, String nickName){
        if(nickName.trim().length()<2) throw new ContactException.ShortContactNameException("Nickname should be minimum 2 characters");
        Contact contact = getContactById(contactId);
        contact.setNickName(nickName);
        return contactRepo.save(contact);
    }

    @Transactional
    public Contact incrementUnreadMessages(Contact contact) {
        System.out.println("Ins. loop 2");
        contact.setUnreadMessages(contact.getUnreadMessages()+1);
        System.out.println("Ins. loop 3");
        return contactRepo.save(contact);
    }

    public Contact addNewUserContactManually(User owner, User contactPerson){
        Contact contact = new Contact();

        contact.setType(RecipientType.USER);
        contact.setUnreadMessages(0);
        contact.setNickName(contactPerson.getUsername());
        contact.setAddedDate(LocalDate.now());

        contact.setOwner(owner);
        owner.getContacts().add(contact);

        contact.setContactPerson(contactPerson);
        contactPerson.getContactOf().add(contact);

        return contactRepo.save(contact);
    }

    @Transactional
    public Contact addNewGroupContactManually(User owner, Group contactGroup){
        Contact contact = new Contact();

        contact.setType(RecipientType.GROUP);
        contact.setUnreadMessages(0);
        contact.setNickName(contactGroup.getGroupName());
        contact.setAddedDate(LocalDate.now());

        contact.setOwner(owner);
        owner.getContacts().add(contact);

        contact.setContactGroup(contactGroup);
        contactGroup.getContactOf().add(contact);

        contact = contactRepo.save(contact);
        userRepo.save(owner);
        groupRepo.save(contactGroup);
        return contact;
    }

    /**
     * Updates the contact's LastSeen, Unread Counter
     */
    public void processContactsForIncomingMessage(int contactId, Message message){
        Contact contact = getContactById(contactId);
        User sender = contact.getOwner();

        RecipientType recipientType = contact.getType();        // based on contactId & recipient type - set recipient
        if(recipientType == RecipientType.USER){
            User contactPerson = contact.getContactPerson();
            if(sender != contactPerson){
                // Reverse-Contact not exists - Need to create new Contact(Reverse contact)
                if(!existsContact(contactPerson, sender)) {
                    //  First link the message to sender's contact
                    linkMessage(contact,message);

                    //  Contact-person is the owner and vice-versa - to create new reverse contact
                    //  & Contact Update - sent to real time update
                    Contact newlySavedReverseContact = addNewUserContactManually(contactPerson, sender);
                    newlySavedReverseContact = incrementUnreadMessages(newlySavedReverseContact);
                    newlySavedReverseContact = linkMessage(newlySavedReverseContact,message);
                    sendNewContactMessageToUser(contactPerson,newlySavedReverseContact);
                }else{
                    // Getting Reverse contact to save unread message
                    Contact reverseContact = getContactByOwnerAndContactPerson(contactPerson,sender);
                    reverseContact = incrementUnreadMessages(reverseContact);

                    // Linking message for both users(Reverse contact & contact)
                    linkMessage(reverseContact,message);
                    linkMessage(contact,message);
                }
            }else{
                // User's own Contact
                linkMessage(contact,message);
            }
        } else if (recipientType == RecipientType.GROUP) {

            Group contactGroup = contact.getContactGroup();
            List<GroupMembership> activeMemberships = groupMembershipRepo.findActiveMembers(contactGroup);
            for(GroupMembership membership: activeMemberships){
                User member = membership.getGroupMemberId();
                //if(member.equals(sender)) continue;
                Contact user_GroupContact = getContactByOwnerAndContactGroup(member,contactGroup);
                System.out.println("Grp-update-Contact ID:" +user_GroupContact);
                user_GroupContact = incrementUnreadMessages(user_GroupContact);

                // Linking message for recipients
                linkMessage(user_GroupContact,message);
            }
        }
        updateLastSeen(contact);
    }

    Contact linkMessage(Contact contact, Message message){
        contact.setLastMessage(message);
        return contactRepo.save(contact);
    }

    public void sendUpdatedContactToAllRecipients(int contactId){
        Contact contact = getContactById(contactId);

        if(contact.getType()==RecipientType.USER){
            // Send Updated Contacts to Owner and Contact-Person respectively
            sendUpdatedContactMessageToUser(contact.getOwner(),contact);

            Contact reverseContact = getContactByOwnerAndContactPerson(contact.getContactPerson(), contact.getOwner());
            sendUpdatedContactMessageToUser(reverseContact.getOwner(),reverseContact);

        } else if (contact.getType()==RecipientType.GROUP) {

            Group contactGroup = contact.getContactGroup();
            Set<GroupMembership> allMembers = contactGroup.getMembers();
            for(GroupMembership membership: allMembers){
                User member = membership.getGroupMemberId();
                Contact user_GroupContact = getContactByOwnerAndContactGroup(member,contactGroup);
                //if(user_GroupContact.equals(contact)) continue;
                sendUpdatedContactMessageToUser(member,user_GroupContact);
            }
        }
    }

    @Transactional
    public void sendNewContactMessageToUser(User member, Contact contact){
        ContactDetailsDto contactDetailsDto = contactDetailsMapper.retrieveContactDetails(contact);
        String dest = "/user/"+member.getId().toString()+"/queue/newContact";
        simpMessagingTemplate.convertAndSend(dest,contactDetailsDto);
    }

    public void sendUpdatedContactMessageToUser(User member, Contact contact){
        System.out.println("Member name:"+member.getUsername());
        ContactDetailsDto contactDetailsDto = contactDetailsMapper.retrieveContactDetails(contact);
        String dest = "/user/"+member.getId().toString()+"/queue/updatedContact";
        simpMessagingTemplate.convertAndSend(dest,contactDetailsDto);
    }

    public void sendUpdatedMemberInfoToGroup(User user, Group group){
        System.out.println("User name:"+user.getUsername());
        System.out.println("Group name:"+group.getGroupName());
        UserDetailsDto userDetailsDto = userDetailMapper.getUserDetails(user);
        String dest = "/group/"+group.getId().toString()+"/queue/updates";
        simpMessagingTemplate.convertAndSend(dest,userDetailsDto);
    }

}
