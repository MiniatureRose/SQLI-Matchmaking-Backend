package com.sqli.matchmaking.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
public class User {

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

    /*
    @Lob
    @Column(name = "profile_image", columnDefinition = "BLOB")
    private byte[] profileImage;

    public void setImageFromPath(String path) throws Exception {
        byte[] imageBytes = getImageBytes(path);
        this.setProfileImage(imageBytes);
    }
    */

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "role")
    private String role;


    public static byte[] getImageBytes(String imagePath) throws Exception {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }

}
