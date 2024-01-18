package com.sqli.matchmaking.dtos;

import java.util.List;
import java.util.stream.Collectors;

import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;

import lombok.Value;

public final class ResponseDTOs {

    @Value
    public static final class UserDetails {
        private final Long id;
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String phone;
        private final Double rank;
        private final String profileImage;
        private final String role;
    
        public UserDetails(User user) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.rank = user.getRank();
            this.profileImage = user.getProfileImage();
            this.role = user.getRole();
        }
    }

    @Value
    public static final class TeamDetails {
        private final Long id;
        private final String name;
        private final Integer score;
        private final List<UserDetails> players;
    
        public TeamDetails(List<User> players, Team team) {
            this.id = team.getId();
            this.name = team.getName();
            this.score = team.getScore();
            this.players = players.stream().map(p -> new UserDetails(p))
                .collect(Collectors.toList());
        }
    }

}