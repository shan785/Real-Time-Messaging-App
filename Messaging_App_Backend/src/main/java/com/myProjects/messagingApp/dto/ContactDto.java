package com.myProjects.messagingApp.dto;

public class ContactDto {

    private Long contactUserId;
    private String contactUserName;

    public ContactDto(Long contactUserId, String contactUserName) {
        this.contactUserId = contactUserId;
        this.contactUserName = contactUserName;
    }

    public ContactDto() {
    }

    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getContactUserName() {
        return contactUserName;
    }

    public void setContactUserName(String contactUserName) {
        this.contactUserName = contactUserName;
    }
}
