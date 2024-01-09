package com.sqli.matchmaking.controller;

import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.standalone.UserRepository;
import com.sqli.matchmaking.request.DTOs;
import com.sqli.matchmaking.service.standalone.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@RequestBody DTOs.Signin loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Find the user by email and get the ID
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            return ResponseEntity.ok().body(Map.of(
                    "message", "User signed in successfully!",
                    "userId", user.getId() // Include the user ID in the response
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody DTOs.Signup request) {
        if (userService.emailAlreadyExists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email already exits"));
        }
        // TODO: Check the structure
        // TODO: send email verification
        User el = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        userService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "User signed up successfully!"));
    }
}
