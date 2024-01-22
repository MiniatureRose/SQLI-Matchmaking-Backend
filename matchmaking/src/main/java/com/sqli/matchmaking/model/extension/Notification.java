package com.sqli.matchmaking.model.extension;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.Instant;

import com.sqli.matchmaking.model.standalone.User;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user; 

    @Column(name = "date_created")
    private Instant dateCreated; 

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    private String message; 

}
