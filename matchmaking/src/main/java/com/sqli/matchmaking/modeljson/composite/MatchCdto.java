package com.sqli.matchmaking.modeljson.composite;

import java.time.Duration;
import java.time.Instant;


import lombok.Data;

@Data
public class MatchCdto {

    private String name;
    private Long organizer_id;
    private Long field_id;
    private Long sport_id;
    private Instant date;
    private Duration duration;
    private Integer noPlayers;
}
