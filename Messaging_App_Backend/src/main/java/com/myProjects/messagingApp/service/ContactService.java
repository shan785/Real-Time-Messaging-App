package com.myProjects.messagingApp.service;

import com.myProjects.messagingApp.config.security.CustomUserDetails;
import com.myProjects.messagingApp.dto.AddContactRequestDto;
import com.myProjects.messagingApp.dto.ContactDto;
import com.myProjects.messagingApp.entity.ContactEntity;
import com.myProjects.messagingApp.entity.UserEntity;
import com.myProjects.messagingApp.repository.ContactRepo;
import com.myProjects.messagingApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private ContactRepo contactRepo;

    private UserRepo userRepo;

    @Autowired
    public void setContactRepo(ContactRepo contactRepo) {
        this.contactRepo = contactRepo;
    }

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public ResponseEntity<List<ContactDto>> getContacts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        //current user id fetched from the security context
        Long currentUserId = userDetails.getId();

        return new ResponseEntity<>(contactRepo.findByOwnerUserId(currentUserId), HttpStatus.FOUND);
    }


    public ResponseEntity<?> addToContacts(AddContactRequestDto addContactRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();

        if(!userRepo.existsByEmail(addContactRequestDto.getContactUserEmail())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String contactEmail = addContactRequestDto.getContactUserEmail();
        UserEntity contactUser = userRepo.findByEmail(contactEmail);

        List<ContactDto> contactList = contactRepo.findByOwnerUserId(currentUserId);

        for(ContactDto contact : contactList) {
            if(contact.getContactUserId() == contactUser.getId())
                return new ResponseEntity<>(new ContactDto(contactUser.getId(), contactUser.getName()), HttpStatus.CONFLICT);
        }

        contactRepo.save(new ContactEntity(currentUserId, contactUser.getId(), contactUser.getName()));
        return ResponseEntity.ok().build();
    }
}
