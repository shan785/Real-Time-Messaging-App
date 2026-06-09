package com.myProjects.messagingApp.service;

import com.myProjects.messagingApp.config.security.CustomUserDetails;
import com.myProjects.messagingApp.dto.MessageDto;
import com.myProjects.messagingApp.entity.MessageEntity;
import com.myProjects.messagingApp.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private MessageRepo messageRepo;

    @Autowired
    public void setMessageRepo(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public ResponseEntity<List<MessageDto>> getMessages(Long contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long currentUserId = userDetails.getId();

        return ResponseEntity.ok(messageRepo.getMessages(currentUserId, contactId));
    }

    public ResponseEntity<HttpStatus> uploadMessage(MessageDto messageDto) {

        messageRepo.save(new MessageEntity(messageDto.getSenderId(), messageDto.getReceiverId(),
                                     messageDto.getContent(), messageDto.getTimestamp()));

        return ResponseEntity.ok().build();

    }
}
