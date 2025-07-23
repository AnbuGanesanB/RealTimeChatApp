package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.configuration.ProfilePicConfig;
import com.example.RealTimeChatApplication.exception.UserException;
import com.example.RealTimeChatApplication.mapper.UserDetailMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.user.*;
import com.example.RealTimeChatApplication.repositories.ContactRepo;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;
    private final ContactService contactService;
    private final FileService fileService;
    private final UserDetailMapper userDetailMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GroupMembershipService groupMembershipService;
    private final ProfilePicConfig profilePicConfig;

    User currentUser = null;

    public ResponseEntity<Map<String, String>> authenticateUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailId(), loginDto.getPassword()));

            System.out.println("Authentication result: " + authentication);

            currentUser = (User)authentication.getPrincipal();
            String token = jwtService.generateToken(currentUser);
            System.out.println(currentUser.getUsername()+":"+token);

            Map<String,String> response = new HashMap<>();
            response.putIfAbsent("token",token);
            response.putIfAbsent("emailId",currentUser.getEmailId());

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (BadCredentialsException ex) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid email or password.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void createNewUser(RegisterDto registerDto) {
        //Check if Email-ID exists
        if (userRepo.existsByEmailId(registerDto.getEmailId())) {
            throw new UserException.EmailAlreadyExistsException("Provided Email-Id not seems yours!. Please provide your Mail-Id");
        }

        User user = new User();
        user.setEmailId(registerDto.getEmailId());
        user.setPassword(new BCryptPasswordEncoder().encode(registerDto.getPassword()));
        user.setUserName(registerDto.getUserName());
        user.setType(RecipientType.USER);
        user.setOnlineStatus(OnlineStatus.OFFLINE);
        user.setAboutMe("Hello! Let's Talk...");
        user = userRepo.save(user);
        contactService.addNewUserContactManually(user,user);        // Creating self-contact for every new user
    }

    public User updateDisplayProfile(User user, String name, String aboutMe, boolean isDpChanged, MultipartFile profilePic){

        if(name.trim().length()<2) throw new UserException.ShortUserNameException("Username should be minimum 2 characters");

        user.setUserName(name);
        user.setAboutMe(aboutMe.isBlank() ? "Hello! Let's Talk..." : aboutMe);
        if(isDpChanged){
            if(profilePic==null){
                user.setDpPath(null);
            }else{
                if (!profilePicConfig.getAllowedTypes().contains(profilePic.getContentType())) {
                    throw new UserException.FileTypeMismatchException("File type must be JPG or PNG only");
                }
                if(profilePic.getSize()>profilePicConfig.getMaxSize().toBytes()){
                    throw new UserException.FileOverSizeException("Uploaded file size must be less than "+profilePicConfig.getMaxSize());
                }
                user = fileService.setProfilePicture(profilePic,user);
            }
        }
        return userRepo.save(user);
    }

    public void sendUpdatedProfileDetails(User user) {
        // Sending update to user's friends
        user.getContactOf().parallelStream().forEach(contact -> {
            contactService.sendUpdatedContactMessageToUser(contact.getOwner(), contact);
        });
        //  Sending update to all groups where user was enrolled.
        //  (regardless of Membership status, As FE won't be subscribed to the Inactive membership-group)
        groupMembershipService.getGroupMemberShips(user).forEach(groupMembership -> {
            Group group = groupMembership.getGroupId();
            contactService.sendUpdatedMemberInfoToGroup(user,group);
        });
    }

    public User changeOnlineStatus(User user, OnlineStatus onlineStatus){
        user.setOnlineStatus(onlineStatus);
        return userRepo.save(user);
    }

    public User changeOnlineStatus(User user, int statusIndex){
        user.setOnlineStatus(OnlineStatus.fromIndex(statusIndex));
        return userRepo.save(user);
    }

    public List<UserDetailsDto> searchUsersExcludingContacts(String searchTerm, Integer userId) {
        return userRepo.findUsersNotInContacts(searchTerm, userId)
                .stream().map(userDetailMapper::getUserDetails).collect(Collectors.toList());
    }

    public User getUserById(int userId){
        System.out.println("Getting user from Repo with id:"+userId);
        return userRepo.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User)authentication.getPrincipal();
        String username = currentUser.getUsername();
        System.out.println("From context try; User: "+username);
        return currentUser;
    }
}
