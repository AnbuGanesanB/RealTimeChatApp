package com.example.RealTimeChatApplication.controller;

import com.example.RealTimeChatApplication.mapper.ContactDetailsMapper;
import com.example.RealTimeChatApplication.model.contact.AddContactDto;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.ContactDetailsDto;
import com.example.RealTimeChatApplication.model.message.OutMessageDto;
import com.example.RealTimeChatApplication.model.user.LoginUserDto;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.model.user.UserDetailsDto;
import com.example.RealTimeChatApplication.service.ContactService;
import com.example.RealTimeChatApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ContactsController {

    private final UserService userService;
    private final ContactService contactService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ContactDetailsMapper contactDetailsMapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chatcontacts")
    @ResponseBody
    public Set<ContactDetailsDto> getAllChatContacts(@RequestBody Map<String, Integer> loginUserDetails){
        int loginUserId = loginUserDetails.get("loginUserId");
        User loggedUser = userService.getUserById(loginUserId);
        return contactService.retrieveAllChatContacts(loggedUser);
    }


    @GetMapping("/search/newContacts")
    public ResponseEntity<List<UserDetailsDto>> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam Integer userId) {
        List<UserDetailsDto> users = userService.searchUsersExcludingContacts(searchTerm, userId);
        return ResponseEntity.ok(users);
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/addcontact")
    @ResponseBody
    public void addNewContact(@RequestBody AddContactDto addContactDto){

        User owner = userService.getUserById(addContactDto.getSenderId());
        User contactPerson = userService.getUserById(addContactDto.getContactPersonId());

        Contact contact = contactService.addNewUserContactManually(owner,contactPerson);

        //Contact contact = contactService.getContactByOwnerAndContactPerson(owner,contactPerson);
        ContactDetailsDto contactDetailsDto = contactDetailsMapper.retrieveContactDetails(contact);
        String dest = "/user/"+owner.getId().toString()+"/queue/newContact";
        simpMessagingTemplate.convertAndSend(dest,contactDetailsDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/editUser")
    public void getAllChats(@RequestParam(value = "contactId") int contactId,
                            @RequestParam(value = "nickName") String nickName){
        Contact updatedContact = contactService.updateNickName(contactId,nickName);
        contactService.sendUpdatedContactMessageToUser(updatedContact.getOwner(),updatedContact);
    }
}
