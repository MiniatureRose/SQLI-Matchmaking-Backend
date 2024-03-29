package com.sqli.matchmaking.model.associative;

import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;

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
@Table(name = "teamusers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"team_id", "player_id"})
})
public final class TeamUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "player_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "team_id")
    private Team team;

}