package com.sqli.matchmaking.service.composite;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.repository.composite.MatchRepository;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;
import com.sqli.matchmaking.service.teammaking.TeamMaking;
import com.sqli.matchmaking.service.standalone.NotificationService;

import lombok.Getter;

@Service
@Getter
public class MatchService {
    
    @Autowired
    private TeamService teamService;

    /* 
     * Repository
     */
    @Autowired
    private MatchRepository repository;
    @Autowired
    private MatchUserRepository matchUserRepository;
    

    /* 
     * Making
     */
    public void makeTeams(Match match, TeamMaking service) {
        // Get teams for this match
        List<Team> teams = teamService.getMatchTeams(match);
        // Get all match player
        List<User> players = this.getMatchPlayers(match);
        // Make the game!
        service.make(players, teams);
    }

    public void setCureentPlayers(Match match) {
        match.setCurPlayers(this.getMatchPlayers(match).size());
    }

    public void setCureentPlayers(List<Match> matches) {
        for (Match match : matches) setCureentPlayers(match);
    }


    /* 
     * Booleans
     */
    public Boolean isFullfilled(Match match) {
        int curPlayers =  this.getMatchPlayers(match).size();
        return curPlayers == match.getNoPlayers();
    }

    public boolean isFieldAlreadyBooked(Field field, Instant start, Duration duration) {
        List<Match> all = this.getAll();
        this.filterMatchesByField(all, field);
        filterComingMatches(all);
        Instant end = start.plus(duration);
        return all.removeIf(match -> {
                var startMatch = match.getDate();
                var endMatch = match.getDate().plus(match.getDuration());
                return (endMatch.isAfter(start) && startMatch.isBefore(end));
            });
    }


    /* 
     * Filtering
     */
    public void filterMatchesBySport(List<Match> matches, Sport sport) {
        matches.removeIf(match -> !match.getSport().equals(sport));
    }

    public void filterMatchesByField(List<Match> matches, Field field) {
        matches.removeIf(match -> !match.getField().equals(field));       
    }

    public void filterPassedMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isAfter(Instant.now()));
    }

    public void filterComingMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isBefore(Instant.now()));
    }

    public MatchUser getByMatchAndUser(Match match, User user){
        return matchUserRepository.findByMatchAndUser(match, user);
    }

    public List<User> getMatchPlayers(Match match) {
        return matchUserRepository.findUsersByMatch(match);
    }

    public List<Match> getUserMatches(User user) {
        return matchUserRepository.findMatchOfUser(user);
    }

    public List<Match> getUserNoMatches(User user) {
        return matchUserRepository.findMatchOfNoUser(user);
    }


    /* 
     * Basic 
     */
    public List<Match> getAll() {
        return repository.findAll();
    }

    public Match getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Match el) {
        repository.save(el);
    }

    public void save(MatchUser el) {
        matchUserRepository.save(el);
    }

    public void delete(Match el) {
        // Remove all matchUsers having el
        List<MatchUser> matchUsers = matchUserRepository.findByMatch(el);
        matchUserRepository.deleteAll(matchUsers);
        // Select all teams having el
        List<Team> teams = teamService.getMatchTeams(el);
        for (Team team : teams) teamService.delete(team);
        // Then remove el
        repository.delete(el);
    }

    public void delete(MatchUser el) {
        matchUserRepository.delete(el);
    }

}
