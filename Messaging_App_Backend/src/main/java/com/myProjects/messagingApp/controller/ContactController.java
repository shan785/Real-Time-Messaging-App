package com.myProjects.messagingApp.controller;

import com.myProjects.messagingApp.dto.AddContactRequestDto;
import com.myProjects.messagingApp.dto.ContactDto;
import com.myProjects.messagingApp.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private ContactService contactService;

    @Autowired
    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactDto>> getContacts() {

        return contactService.getContacts();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToContacts(@RequestBody AddContactRequestDto addContactRequestDto) {
        return contactService.addToContacts(addContactRequestDto);
    }
}
