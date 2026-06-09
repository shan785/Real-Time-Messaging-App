package com.myProjects.messagingApp.util;

import com.myProjects.messagingApp.dto.MessageDto;
import com.myProjects.messagingApp.enums.ActiveStatus;
import com.myProjects.messagingApp.enums.PayloadType;

import java.util.List;

public class CustomPayload {

    private Long contactUserId;
    private List<Long> onlineUsers;
    private PayloadType payloadType;
    private ActiveStatus activeStatus;
    private MessageDto messageDto;

    public CustomPayload() {
    }

    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public List<Long> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<Long> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public ActiveStatus getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(ActiveStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    public MessageDto getMessageDto() {
        return messageDto;
    }

    public void setMessageDto(MessageDto messageDto) {
        this.messageDto = messageDto;
    }
}
