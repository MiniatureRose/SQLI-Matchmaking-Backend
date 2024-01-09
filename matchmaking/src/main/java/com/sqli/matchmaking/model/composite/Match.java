package com.sqli.matchmaking.model.composite;

import java.time.Instant;
import java.time.Duration;

import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;
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
@Table(name = "matches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"field_id", "date"})
})
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @ManyToOne()
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne()
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private Instant date;

    @Column(name = "duration")
    private Duration duration;

    @Column(name = "no_players")
    private Integer noPlayers;

}
