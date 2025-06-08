package com.example.RealTimeChatApplication.configuration;

import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {

        User user = userRepo.findByEmailId(emailId)
                .orElseThrow(()->new UsernameNotFoundException("User Not found with email: "+emailId));
        return user;
    }
}
