package com.sqli.matchmaking.controller.auth;

// utils
import java.util.Map;
// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
// spring (security)
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
// dtos
import com.sqli.matchmaking.dtos.RequestDTOs;
// entities
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.standalone.UserService;


@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;


    @PostMapping("/signin")
    @Transactional
    public ResponseEntity<Object> signin(@RequestBody RequestDTOs.Signin loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Find the user by email and get the ID
            User user = userService.getByEmail(loginDto.getEmail());
            // Return
            return ResponseEntity.ok().body(Map.of(
                    "message", "User signed in successfully!",
                    "userId", user.getId() // Include the user ID in the response
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Authentication failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<Object> signup(@RequestBody RequestDTOs.Signup request) {
        if (userService.emailAlreadyExists(request.getEmail())) {
            return ResponseEntity.ok()
                .body(Map.of("message", "Email already exits!"));
        }
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
