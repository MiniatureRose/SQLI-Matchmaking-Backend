package com.sqli.matchmaking.service.extension.teammaking.forms;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.model.associative.TeamUser;
import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.extension.TeamService;
import com.sqli.matchmaking.service.extension.teammaking.TeamMaking;



@Service
public class RandomMaking implements TeamMaking {

    @Autowired
    private TeamService teamService;
    
    @Transactional
    public void make(List<User> players, List<Team> teams) {

        // Shuffle the list to randomize the order of players
        Collections.shuffle(players);

        // Calculate the number of teams
        int noTeams = teams.size();

        // Calculate the number of players per team
        int playersPerTeam = players.size() / noTeams;

        // Split the players into teams
        for (int i = 0; i < noTeams; i++) {
            // Determine the start and end indices for this team in the list
            int start = i * playersPerTeam;
            int end = (i + 1) * playersPerTeam;

            // Create a sublist for the team
            List<User> team = players.subList(start, end);

            for (User player : team) {
                TeamUser el = TeamUser.builder()
                                .user(player).team(teams.get(i))
                                .build();
                try {
                    teamService.save(el);
                } catch (DataIntegrityViolationException e) {
                    throw new Exceptions.EntityCannotBeSaved("TeamUser");
                }        
            }

        }
    }
}
