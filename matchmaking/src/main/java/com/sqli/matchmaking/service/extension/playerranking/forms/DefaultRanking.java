package com.sqli.matchmaking.service.extension.playerranking.forms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.extension.TeamService;
import com.sqli.matchmaking.service.extension.playerranking.PlayerRanking;
import com.sqli.matchmaking.service.standalone.UserService;

@Service
public class DefaultRanking implements PlayerRanking {

    @Autowired 
    private TeamService teamService;
    @Autowired 
    private UserService userService;

    private final Integer GAIN = 50;


    @Transactional
    public void rank(List<Team> teams) {
        for (Team team : teams) {
            Double averageRank = teamService.getAverageRank(team);
            Double vsAverageRank = teamService.getVsAverageRank(team);
            List<User> players = teamService.getTeamPlayers(team);
            for (User player : players) {
                Double evo1 = 0.2*(vsAverageRank - averageRank);
                Double evo2 = 0.2*(vsAverageRank - player.getRank());
                Integer evo3 = team.getScore() - teamService.getVsAverageScore(team);
                Double evo = evo1 + evo2 + evo3;
                if (teamService.isWinner(team)) {
                    Double diff = GAIN + evo;
                    Double newRank = player.getRank() + diff;
                    userService.updateRank(player, (int) Math.round(newRank));
                }
                else if (teamService.isLoser(team)) {
                    Double diff = -GAIN + evo;
                    Double newRank = player.getRank() + diff;
                    userService.updateRank(player, (int) Math.round(newRank));
                }
            }
        }
    }

}