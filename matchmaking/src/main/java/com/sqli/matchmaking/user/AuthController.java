package com.sqli.matchmaking.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.sqli.matchmaking.Repository.UserRepository;
import com.sqli.matchmaking.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository UserRepository;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signin")
    public ResponseEntity<Response> authenticateUser(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String successMessage = "User signed in successfully!";
            Response response = new Response(true, successMessage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException e) {
            String errorMessage = "Authentication failed";
            Response response = new Response(false, errorMessage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupDto signUpDto){

        // add check for email exists in DB
        if(UserRepository.existsByEmail(signUpDto.getEmail())){
            Response response = new Response(true, "Email is already taken!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // create user object
        User user = new User();
        user.setFirstname(signUpDto.getFirstname());
        user.setLastname(signUpDto.getLastname());
        user.setPhone(signUpDto.getPhone());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole("user");

        UserRepository.save(user);
        Response response = new Response(true, "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // Classe pour représenter la réponse JSON
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Response {
        private boolean success;
        private String message;

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}