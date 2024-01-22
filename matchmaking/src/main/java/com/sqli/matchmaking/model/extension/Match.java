package com.sqli.matchmaking.model.extension;

import java.time.Instant;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        @UniqueConstraint(columnNames = { "field_id", "date" })
})
public final class Match {

    // status
    public static final String RECORDED = "RECORDED";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String FORMED = "FORMED";
    public static final String CLOSED = "CLOSED";
    public static final String PENDING = "PENDING";
    public static final String CANCELED = "CANCELED";

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

    @Column(name = "noSubs")
    private Integer noSubs;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "status")
    private String status = PENDING;


    @JsonIgnore
    public Boolean isPassed() {
        return Instant.now().isAfter(this.date.plus(this.duration));
    }

}
