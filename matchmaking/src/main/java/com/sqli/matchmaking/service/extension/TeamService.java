package com.sqli.matchmaking.service.extension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.exception.Exceptions.EntityIsNull;
import com.sqli.matchmaking.model.associative.TeamUser;
import com.sqli.matchmaking.model.extension.Match;
import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.associative.TeamUserRepository;
import com.sqli.matchmaking.repository.extension.TeamRepository;
import com.sqli.matchmaking.service.extension.playerranking.PlayerRanking;

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
    @Transactional
    public void createTeams(Match match) {
        // Create and save teams for this match
        int noTeams = match.getSport().getNoTeams();
        for (int i = 0; i < noTeams; i++) {
            // Create team
            Team team = Team.builder()
                        .name(String.valueOf(i))
                        .match(match)
                        .build();
            if (team == null) {
                throw new EntityIsNull("Team");
            }
            // Save it
            try {
                repository.save(team);
            } catch (DataIntegrityViolationException e) {
                throw new Exceptions.EntityCannotBeSaved("Team");
            }
        }
    }


    /* 
     * Ranking
     */
    public Double getAverageRank(Team team) {
        List<User> players = this.getTeamPlayers(team);
        List<Integer> ranks = players.stream().map(User::getRank).collect(Collectors.toList());
        Double average = ranks.stream()
                              .mapToInt(Integer::intValue)
                              .average()
                              .orElse(0.0);
        return average;
    }

    public Double getVsAverageRank(Team team) {
        List<Team> vsTeams = this.getVsTeam(team);
        List<Double> ranks = vsTeams.stream().map(t -> getAverageRank(t)).collect(Collectors.toList());
        double average = ranks.stream()
                              .mapToDouble(Double::doubleValue)
                              .average()
                              .orElse(0.0);
        return average;
    }

    public Integer getVsAverageScore(Team team) {
        List<Team> vsTeams = this.getVsTeam(team);
        List<Integer> scores = vsTeams.stream().map(Team::getScore).collect(Collectors.toList());
        double average = scores.stream()
                               .mapToInt(Integer::intValue)
                               .average()
                               .orElse(0.0);
        return (int) average; // Convert double to Integer
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

    @Transactional
    public void deleteMatchTeams(Match match) {
        List<Team> teams = this.getMatchTeams(match);
        teams.forEach(team -> {
            if (team == null) {
                throw new EntityIsNull("Team");
            }
            this.delete(team);
        });
    }

    public List<User> getTeamPlayers(Team team) {
        return teamUserRepository.findUsersByTeam(team);
    }

    public List<Team> getVsTeam(Team team) {
        List<Team> all = this.getMatchTeams(team.getMatch());
        all.remove(team);
        return all;
    }



    /* 
     * Basic 
     */
    public List<Team> getAll() {
        return repository.findAll();
    }

    public Team getById(Long id) {
        if (id == null) {
            throw new EntityIsNull("Team");
        }
        return repository.findById(id)
            .orElseThrow(() -> 
                new Exceptions.EntityNotFound("Team", "id", id));
    }

    public void save(Team el) {
        if (el == null) {
            throw new EntityIsNull("Team");
        }
        try {
            repository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Team");
        }
    }

    public void save(TeamUser el) {
        if (el == null) {
            throw new EntityIsNull("TeamUser");
        }
        try {
            teamUserRepository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("TeamUser");
        }
    }

    public void updateScore(Team el, int score) {
        try {
            el.setScore(score);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated("Team", "score");
        }
    }

    @Transactional
    public void delete(Team el) {
        if (el == null) {
            throw new Exceptions.EntityIsNull("Team");
        }
        // Remove all teamusers having el
        List<TeamUser> teamUsers = teamUserRepository.findByTeam(el);
        if (teamUsers == null) { 
            throw new EntityIsNull("List<TeamUser>");
        }
        try {
            teamUserRepository.deleteAll(teamUsers);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeDeleted("All TeamUser");
        }
        // Then remove team
        try {
            repository.delete(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeDeleted("Team");
        }
    }

}
