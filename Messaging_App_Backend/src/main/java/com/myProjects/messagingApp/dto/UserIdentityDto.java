package com.myProjects.messagingApp.dto;

public class UserIdentityDto {

    private Long id;

    public UserIdentityDto(Long id) {
        this.id = id;
    }

    public UserIdentityDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
