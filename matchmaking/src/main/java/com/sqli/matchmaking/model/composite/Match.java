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

    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELED = "CANCELED";
    public static final String PENDING = "PENDING";

    @Builder.Default
    @Column(name = "is_confirmed")
    private String status = PENDING;

    @Builder.Default
    @Column(name = "cur_players")
    private Integer curPlayers = 0;

    
    public void join() {
        this.curPlayers ++;
    }

    public void unjoin() {
        this.curPlayers --;
    }

    public Boolean isFullfilled() {
        return this.curPlayers == this.noPlayers;
    }

    public Boolean isConfirmed() {
        return this.status == CONFIRMED;
    }

    public Boolean isCanceled() {
        return this.status == CANCELED;
    }

    public void confirm() {
        this.status = CONFIRMED;
    }

    public void cancel() {
        this.status = CANCELED;
    }

}


