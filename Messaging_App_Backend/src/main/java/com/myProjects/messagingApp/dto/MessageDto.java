package com.myProjects.messagingApp.dto;

public class MessageDto {

    private String content;
    private Long timestamp;
    private Long receiverId;
    private Long senderId;

    public MessageDto(String content, Long timestamp, Long receiverId, Long senderId) {
        this.content = content;
        this.timestamp = timestamp;
        this.receiverId = receiverId;
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}
