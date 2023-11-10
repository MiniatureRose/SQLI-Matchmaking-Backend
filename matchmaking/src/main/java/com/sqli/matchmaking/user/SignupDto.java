package com.sqli.matchmaking.user;

import lombok.Data;

@Data
public class SignupDto {
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String password;
}
