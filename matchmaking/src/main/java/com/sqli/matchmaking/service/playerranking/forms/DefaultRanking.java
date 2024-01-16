package com.sqli.matchmaking.service.playerranking.forms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.composite.TeamService;
import com.sqli.matchmaking.service.playerranking.PlayerRanking;

@Service
public class DefaultRanking implements PlayerRanking {

    @Autowired 
    private TeamService teamService;

    
    public void rank(List<Team> teams) {
        for (Team team : teams) {
            Double averageRank = teamService.getAverageRank(team);
            List<User> players = teamService.getTeamPlayers(team);
            for (User player : players) {
                if (teamService.isWinner(team)) {
                    Double diff = 50 + 0.1*averageRank;
                    player.setRank(player.getRank() + diff);
                }
                else if (teamService.isLoser(team)) {
                    Double diff = 50 + 0.1*averageRank;
                    player.setRank(player.getRank() - diff);
                }
            }
        }
    }

}
