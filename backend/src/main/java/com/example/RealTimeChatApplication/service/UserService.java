package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.mapper.UserDetailMapper;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    User currentUser = null;

    public User validate(String emailId, String password) {
        Optional<User> optUser = userRepo.findByEmailId(emailId);

        if(optUser.isEmpty()) throw new RuntimeException("No User Found");
        User user = optUser.get();

        if(password.equals(user.getPassword())) return user;
        else throw new RuntimeException("Password Mismatch for Email: "+emailId);
    }

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
            errorResponse.put("error", "Invalid email or password.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

        } catch (DisabledException ex) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Your account is disabled. Please contact support.");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public void createNewUser(RegisterDto registerDto) {
        User user = new User();
        user.setEmailId(registerDto.getEmailId());
        user.setPassword(new BCryptPasswordEncoder().encode(registerDto.getPassword()));
        user.setUserName(registerDto.getUserName());
        user.setType(RecipientType.USER);
        user.setOnlineStatus(OnlineStatus.OFFLINE);
        user = userRepo.save(user);
        contactService.addNewUserContactManually(user,user);        // Creating self-contact for every new user
    }


    public User updateDisplayProfile(User user, String name, String aboutMe, boolean isDpChanged, MultipartFile profilePic){
        user.setUserName(name);
        user.setAboutMe(aboutMe);
        if(isDpChanged){
            if(profilePic==null){
                user.setDpPath(null);
            }else{
                user = fileService.setProfilePicture(profilePic,user);
            }
        }
        return userRepo.save(user);
    }

    public void sendUpdatedProfileDetails(User user) {
        user.getContactOf().parallelStream().forEach(contact -> {
            contactService.sendUpdatedContactMessageToUser(contact.getOwner(), contact);
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
