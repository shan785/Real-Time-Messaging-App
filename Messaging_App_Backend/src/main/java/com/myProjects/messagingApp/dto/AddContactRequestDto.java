package com.myProjects.messagingApp.dto;

public class AddContactRequestDto {

    private String contactUserEmail;

    public AddContactRequestDto(String contactUserEmail) {
        this.contactUserEmail = contactUserEmail;
    }

    public String getContactUserEmail() {
        return contactUserEmail;
    }
}
