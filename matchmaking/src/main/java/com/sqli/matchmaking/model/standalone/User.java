package com.sqli.matchmaking.model.standalone;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"first_name", "last_name"})
})
public final class User {

    // roles
    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Builder.Default
    @Column(name = "ranking")
    private Double rank = 1000.;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "role")
    private String role;



    public Boolean isAdmin() {
        return this.role.equals(ADMIN);
    }

    public Boolean isUser() {
        return this.role.equals(USER);
    }

}
