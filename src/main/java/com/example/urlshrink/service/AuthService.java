package com.example.urlshrink.service;

import com.example.urlshrink.exceptions.DuplicateKeyErrorException;
import com.example.urlshrink.exceptions.UserNotFoundException;
import com.example.urlshrink.model.User;
import com.example.urlshrink.model.dtos.AuthRequestDto;
import com.example.urlshrink.model.dtos.AuthResponseDto;
import com.example.urlshrink.model.dtos.RegistrationRequestDto;
import com.example.urlshrink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;


    private void checkIfUserAlreadyExists(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new DuplicateKeyErrorException(String.format("The User with the email: %s already exists. Please try another one.", email));
        }
    }
    public AuthResponseDto registerUser(RegistrationRequestDto registrationRequestDto) {
        checkIfUserAlreadyExists(registrationRequestDto.getEmail());
        User user = User.builder()
                .firstname(registrationRequestDto.getFirstname())
                .lastname(registrationRequestDto.getLastname())
                .password(this.passwordEncoder.encode(registrationRequestDto.getPassword()))
                .email(registrationRequestDto.getEmail())
                .build();
        userRepository.save(user);
        return AuthResponseDto.builder().token(this.tokenService.generateToken(user)).build();
    }

    public AuthResponseDto authenticate(AuthRequestDto authRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDto.getEmail(),
                        authRequestDto.getPassword()
                )
        );
        try {
            User user = userRepository.findByEmail(authRequestDto.getEmail());
            if(user == null) {
                throw new UserNotFoundException(String.format("User with the email: %s not found", authRequestDto.getEmail()));

            }
            return AuthResponseDto.builder().token(this.tokenService.generateToken(user)).build();

        } catch(NoSuchElementException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
