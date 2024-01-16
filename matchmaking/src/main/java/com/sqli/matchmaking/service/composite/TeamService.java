package com.sqli.matchmaking.service.composite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.composite.TeamUser;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.composite.TeamRepository;
import com.sqli.matchmaking.repository.composite.TeamUserRepository;
import com.sqli.matchmaking.service.playerranking.PlayerRanking;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
public class TeamService {

    /*
     * Repository
     */
    @Autowired
    private TeamRepository repository;
    @Autowired
    private TeamUserRepository teamUserRepository;


    /* 
     * Auto
     */
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
            repository.save(team);
        }
    }


    /* 
     * Ranking
     */
    public Double getAverageRank(Team team) {
        List<User> players = this.getTeamPlayers(team);
        List<Double> ranks = players.stream().map(User::getRank).collect(Collectors.toList());
        Double average = ranks.stream()
                              .mapToDouble(Double::doubleValue)
                              .average()
                              .orElse(0.0);
        return average;
    }

    public void rankPlayers(Match match, PlayerRanking service) {
        // Get teams for this match
        List<Team> teams = this.getMatchTeams(match);
        // Rank
        service.rank(teams);
    }


    /* 
     * Booleans
     */
    public Boolean isDraw(Team team) {
        Match match = team.getMatch();
        List<Team> teams = getMatchTeams(match);
        return teams.stream().allMatch(t -> t.getScore() == team.getScore());
    }

    public Boolean isWinner(Team team) {
        // TODO: use sport to define what is a winner
        Match match = team.getMatch();
        List<Team> teams = this.getMatchTeams(match);
        return !isDraw(team) && teams.stream()
                    .max(Comparator.comparingInt(Team::getScore))
                    .map(winner -> winner.equals(team))
                    .orElse(false);
    }

    public Boolean isLoser(Team team) {
        return !isDraw(team) && !isWinner(team);
    }


    /* 
     * Filtering
     */
    public List<Team> getMatchTeams(Match match) {
        return repository.findByMatch(match);
    }

    public List<User> getTeamPlayers(Team team) {
        return teamUserRepository.findUsersByTeam(team);
    }


    /* 
     * Basic 
     */
    public List<Team> getAll() {
        return repository.findAll();
    }

    public Team getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Team el) {
        repository.save(el);
    }

    public void save(TeamUser el) {
        teamUserRepository.save(el);
    }

    public void delete(Team el) {
        // Remove all teamusers having el
        List<TeamUser> teamUsers = teamUserRepository.findByTeam(el);
        teamUserRepository.deleteAll(teamUsers);
        // Then remove team
        repository.delete(el);
    }

}
