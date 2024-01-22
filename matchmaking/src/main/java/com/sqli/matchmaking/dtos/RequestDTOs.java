package com.sqli.matchmaking.dtos;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import lombok.Data;

public final class RequestDTOs {

    @Data
    public final class Signup {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String password;
        private final String phone;
        private final String role;
    }

    @Data
    public final class Signin {
        private final String email;
        private final String password;
    }

    @Data
    public final class Match {
        private final Long organizerId;
        private final Long fieldId;
        private final Long sportId;
        private final String name;
        private final Instant date;
        private final Duration duration;
        private final Integer noPlayers;
        private final Integer noSubs;
        private final String description;
    }

    @Data
    public final class MatchUser {
        private final Long userId;
        private final Long matchId;
    }


    @Data
    public final class TeamRecord {
        private final Long teamId;
        private final Integer score;
    }

    @Data
    public final class TeamPlayers {
        private final String teamName;
        private final List<Long> playersIds;
    }
    
    @Data
    public final class FieldSport {
        private final Long fieldId;
        private final Long sportId;
    }

    @Data
    public final class Sport {
        private final String name;
        private final Integer noTeams;
    }

    @Data
    public final class Field {
        private final String name;
        private final String location;
        private final Integer noPlayers;
    }

}
