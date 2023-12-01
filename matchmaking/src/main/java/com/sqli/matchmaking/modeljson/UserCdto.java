package com.sqli.matchmaking.modeljson;

import lombok.Data;

@Data
public class UserCdto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String role;
    
}
