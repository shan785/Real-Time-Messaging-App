package com.myProjects.messagingApp.service;

import com.myProjects.messagingApp.config.security.CustomUserDetails;
import com.myProjects.messagingApp.dto.UserIdentityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public ResponseEntity<UserIdentityDto> getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserIdentityDto userIdentityDto = new UserIdentityDto(userDetails.getId());
        return new ResponseEntity<>(userIdentityDto, HttpStatus.FOUND);

    }


//    private UserRepo userRepo;
//
//    private ContactRepo contactRepo;
//
//    @Autowired
//    public void setContactRepo(ContactRepo contactRepo) {
//        this.contactRepo = contactRepo;
//    }

//    @Autowired
//    public void setUserRepo(UserRepo userRepo) {
//        this.userRepo = userRepo;
//    }
//
//    public ResponseEntity<List<ContactDto>> getContacts() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//        //current user id fetched from the security context
//        return new ResponseEntity<>(userRepo.getContacts(userDetails.getId()), HttpStatus.FOUND);
//    }

//    public ResponseEntity<HttpStatus> addToContacts() {
//
//    }
}
