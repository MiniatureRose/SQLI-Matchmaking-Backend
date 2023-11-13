package com.sqli.matchmaking.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Matchs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    // @Column(value='numbers_of_players');
    // private Integer numbersOfPlayers;
    private Integer numbers_of_players;
    private String location;
    private Integer organizer_id;
    private String date_hour;
}
