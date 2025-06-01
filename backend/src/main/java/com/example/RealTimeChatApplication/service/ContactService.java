package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.ContactRepo;
import com.example.RealTimeChatApplication.repositories.GroupRepo;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return contactRepo.findByOwnerAndContactGroup(owner,contactGroup);
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
        contact.setNickName(contactPerson.getUserName());
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

        return contactRepo.save(contact);
    }


    @Transactional
    public void sendNewContactMessageToUser(User member, Contact contact){
        ContactDetailsDto contactDetailsDto = contactDetailsMapper.retrieveContactDetails(contact);
        String dest = "/user/"+member.getId().toString()+"/queue/newContact";
        simpMessagingTemplate.convertAndSend(dest,contactDetailsDto);
    }

    public void sendUpdatedContactMessageToUser(User member, Contact contact){
        System.out.println("Member name:"+member.getUserName());
        ContactDetailsDto contactDetailsDto = contactDetailsMapper.retrieveContactDetails(contact);
        String dest = "/user/"+member.getId().toString()+"/queue/updatedContact";
        simpMessagingTemplate.convertAndSend(dest,contactDetailsDto);
    }

}
