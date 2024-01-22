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
    private final List<Integer> SIGMA = List.of(
        0, 5, 10, 14, 18, 21, 24, 26, 28, 29, 30
    );


    @Transactional
    public void rank(List<Team> teams) {
        for (Team team : teams) {
            Double averageRank = teamService.getAverageRank(team);
            Double vsAverageRank = teamService.getVsAverageRank(team);
            List<User> players = teamService.getTeamPlayers(team);
            for (User player : players) {
                Double evo1 = 0.2*(vsAverageRank - averageRank);
                Double evo2 = 0.2*(vsAverageRank - player.getRank());
                Integer scoreDiff = team.getScore() - teamService.getVsAverageScore(team);
                Integer evo3 = this.sigma(scoreDiff);
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


    private Integer sigma(Integer x) {
        int n = SIGMA.size();
        int sign = x > 0 ? 1 : -1;
        int idx = Math.abs(x);
        int image = idx < n ? SIGMA.get(idx) : SIGMA.get(n - 1);
        return sign * image;
    }

}
