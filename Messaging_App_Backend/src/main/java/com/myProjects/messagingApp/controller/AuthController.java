package com.myProjects.messagingApp.controller;

import com.myProjects.messagingApp.dto.LoginRequestDto;
import com.myProjects.messagingApp.dto.LoginResponseDto;
import com.myProjects.messagingApp.dto.RegistrationRequestDto;
import com.myProjects.messagingApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto credentials) {
        return authService.login(credentials);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody RegistrationRequestDto details) {

        return authService.register(details);
    }
}
