package com.myProjects.messagingApp.controller;

import com.myProjects.messagingApp.dto.UserIdentityDto;
import com.myProjects.messagingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserIdentityDto> getCurrentUserId() {
        return userService.getCurrentUserId();
    }

}
