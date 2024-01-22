package com.sqli.matchmaking.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sqli.matchmaking.model.extension.Match;
import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.extension.MatchService;
import com.sqli.matchmaking.service.extension.TeamService;

import lombok.Value;

@Component
public final class ResponseDTOs {

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;

    @Value
    public final class UserDetails {
        private final Long id;
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String phone;
        private final Integer rank;
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
    public final class TeamDetails {
        private final Long id;
        private final String name;
        private final Integer score;
        private final List<UserDetails> players;
    
        public TeamDetails(Team team) {
            this.id = team.getId();
            this.name = team.getName();
            this.score = team.getScore();
            this.players = teamService.getTeamPlayers(team).stream().map(p -> new UserDetails(p))
                .collect(Collectors.toList());
        }
    }

    @Value
    public final class MatchDetails {
        private final Match match;
        private Integer curPlayers;
        private final List<TeamDetails> teams;
    
        public MatchDetails(Match match) {
            this.match = match;
            this.curPlayers = matchService.getMatchPlayers(match).size();
            this.teams = teamService.getMatchTeams(match).stream().map(t -> new TeamDetails(t))
                .collect(Collectors.toList());
        }
    }

}