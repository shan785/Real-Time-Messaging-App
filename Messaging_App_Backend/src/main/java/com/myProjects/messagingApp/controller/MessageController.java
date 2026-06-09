package com.myProjects.messagingApp.controller;

import com.myProjects.messagingApp.dto.MessageDto;
import com.myProjects.messagingApp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long contactId) {
        return messageService.getMessages(contactId);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> uploadMessage(@RequestBody MessageDto messageDto) {

        return messageService.uploadMessage(messageDto);
    }

}

