package com.sqli.matchmaking.model.standalone;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.Instant;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "notification")

public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId; 

    private String message; 

    @Column(name = "date_created")
    private Instant dateCreated; 

    @Column(name = "is_read")
    private boolean isRead;


}
