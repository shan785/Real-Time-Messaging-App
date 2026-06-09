package com.myProjects.messagingApp.config.security;

import com.myProjects.messagingApp.entity.UserEntity;
import com.myProjects.messagingApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity authenticatedUser = userRepo.findByEmail(username);

        if(authenticatedUser == null) {
            throw new UsernameNotFoundException("user not found.");
        }

        return new CustomUserDetails(authenticatedUser);
    }
}
