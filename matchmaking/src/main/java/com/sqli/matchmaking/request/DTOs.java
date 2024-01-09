package com.sqli.matchmaking.request;

import java.time.Duration;
import java.time.Instant;

import lombok.Data;


public class DTOs {

    @Data
    public static final class Signup {

        private final String firstName;
        private final String lastName;
        private final String email;
        private final String password;
        private final String phone;
        private final String role;
        
    }

    @Data
    public static final class Signin {

        private final String email;
        private final String password;
        
    }

    @Data
    public static final class Match {

        private final String name;
        private final Long organizerId;
        private final Long fieldId;
        private final Long sportId;
        private final Instant date;
        private final Duration duration;
        private final Integer noPlayers;
    }

    @Data
    public static final class MatchUser {
        
        private final Long userId;
        private final Long matchId;

    }

    @Data
    public static final class TeamUser {
        
        private final Long userId;
        private final Long teamId;
        
    }

    @Data
    public static final class Team {
        
        private final String name;
        private final Long matchId;
        
    }

    @Data
    public static final class ManualMaking {
    
    }

    @Data
    public static final class FieldSport {
        
        private final Long fieldId;
        private final Long sportId;
        
    }


    @Data
    public static final class Sport {
        
        private final String name;
        private final Integer noTeams;
        
    }

    @Data
    public static final class Field {
        
        private final String name;
        private final String location;
        private final Integer noPlayers;
        
    }


    
}


