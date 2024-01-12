package com.sqli.matchmaking.service.composite;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.composite.MatchRepository;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;

@Service
public class MatchService {
    
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchUserRepository matchUserRepository;
    @Autowired
    private TeamService teamService;

    public MatchRepository repository() {
        return matchRepository;
    }
    

    /* 
     * filtering
     */
    public List<Match> getMatches() {
        List<Match> all = new ArrayList<>(matchRepository.findAll());
        return all;
    }

    public List<Match> getMatchesBySport(List<Match> matches, Sport sport) {
        return matches.stream()
                      .filter(match -> match.getSport() != null && match.getSport().equals(sport))
                      .collect(Collectors.toList());
    }

    public List<Match> getMatchesByField(List<Match> matches, Field field) {
        return matches.stream()
                      .filter(match -> match.getField() != null && match.getField().equals(field))
                      .collect(Collectors.toList());
    }

    public void filterPassedMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isAfter(Instant.now()));
    }

    public void filterComingMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isBefore(Instant.now()));
    }


    /* others */
    public boolean isFieldAlreadyBooked(Field field, Instant start, Duration duration) {
        List<Match> all = getMatches();
        List<Match> set = new ArrayList<>(getMatchesByField(all, field));
        filterComingMatches(set);
        Instant end = start.plus(duration);
        return set.removeIf(match -> {
                var startMatch = match.getDate();
                var endMatch = match.getDate().plus(match.getDuration());
                return (endMatch.isAfter(start) && startMatch.isBefore(end));
                });
    }


    /* 
     * basic 
     */
    public Match getById(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    public void save(Match el) {
        matchRepository.save(el);
    }

    public void delete(Match el) {
        // Remove all matchUsers having el
        List<MatchUser> matchUsers = matchUserRepository.findByMatch(el);
        matchUserRepository.deleteAll(matchUsers);
        // Select all teams having el
        List<Team> teams = teamService.repository().findByMatch(el);
        for (Team team : teams) teamService.delete(team);
        // Then remove el
        matchRepository.delete(el);
    }

}
