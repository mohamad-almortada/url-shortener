package com.example.urlshrink.controller;

import com.example.urlshrink.model.dtos.AuthRequestDto;
import com.example.urlshrink.model.dtos.AuthResponseDto;
import com.example.urlshrink.model.dtos.RegistrationRequestDto;
import com.example.urlshrink.service.AuthService;
import com.example.urlshrink.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class AuthenticationController {

    private final TokenService tokenService;
    private final AuthService authService;

    public AuthenticationController(TokenService tokenService, AuthService authService) {
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponseDto>  token(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.authenticate(authRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody RegistrationRequestDto requestDto) {
        return ResponseEntity.ok(this.authService.registerUser(requestDto));
    }

}
