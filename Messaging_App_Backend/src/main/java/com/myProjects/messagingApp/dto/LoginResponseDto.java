package com.myProjects.messagingApp.dto;

public class LoginResponseDto {

    private String jwtToken;
    private Long currentUserId;

    public LoginResponseDto(String jwtToken, Long currentUserId) {
        this.jwtToken = jwtToken;
        this.currentUserId = currentUserId;
    }

    public LoginResponseDto() {
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }
}
