package com.sqli.matchmaking.model.composite;

import com.sqli.matchmaking.model.User;

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
@Table(name = "matchusers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"player_id", "match_id"})
})
public class MatchUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "player_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "match_id")
    private Match match;

}