package com.myProjects.messagingApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class MessageEntity {

    public MessageEntity() {
    }

    public MessageEntity(Long senderId, Long receiverId, String content, Long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private Long timestamp;

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
