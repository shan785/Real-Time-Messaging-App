package com.myProjects.messagingApp.service;

import com.myProjects.messagingApp.config.security.CustomUserDetails;
import com.myProjects.messagingApp.dto.LoginRequestDto;
import com.myProjects.messagingApp.dto.LoginResponseDto;
import com.myProjects.messagingApp.dto.RegistrationRequestDto;
import com.myProjects.messagingApp.entity.UserEntity;
import com.myProjects.messagingApp.repository.UserRepo;
import com.myProjects.messagingApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private UserRepo userRepo;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<LoginResponseDto> login(LoginRequestDto credentials) {

        Authentication authentication = null;
        CustomUserDetails userDetails = null;

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());

            authentication = authenticationManager.authenticate(authToken);
            userDetails = (CustomUserDetails) authentication.getPrincipal();
        }
        catch(AuthenticationException e) {
            //if authentication fails
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = jwtUtil.generateJwtToken(credentials.getEmail());

        return new ResponseEntity<>(new LoginResponseDto(jwtToken, userDetails.getId()), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> register(RegistrationRequestDto details) {

        try {
            if(userRepo.existsByEmail(details.getEmail())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            String encodedPassword = passwordEncoder.encode(details.getPassword());

            userRepo.save(new UserEntity(details.getName(), details.getEmail(), encodedPassword));
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
