package com.myProjects.messagingApp.dto;

public class RegistrationRequestDto {

    private String email;
    private String name;
    private String password;

    public RegistrationRequestDto(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public RegistrationRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
