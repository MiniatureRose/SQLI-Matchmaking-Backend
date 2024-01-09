package com.sqli.matchmaking.service.matchmaking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.composite.MatchUserService;
import com.sqli.matchmaking.service.composite.TeamService;


public abstract class MatchMaking {

    @Autowired
    private TeamService teamService;
    @Autowired
    private MatchUserService matchUserService;


    public abstract void make(List<User> players, List<Team> teams);


    public void createTeams(Match match) {
        // Create and save teams for this match
        int noTeams = match.getSport().getNoTeams();
        for (int i = 0; i < noTeams; i++) {
            // Create team
            Team team = Team.builder()
                        .name(String.valueOf(i))
                        .match(match)
                        .build();
            // Save it
            teamService.save(team);
        }
    }


    public void makeJoin(Match match) {
        // Get teams for this match
        List<Team> teams = teamService.getMatchTeams(match);

        // Get all match player
        List<User> players = matchUserService.getMatchPlayers(match);

        // Make the game!
        this.make(players, teams);
    }
    
}
