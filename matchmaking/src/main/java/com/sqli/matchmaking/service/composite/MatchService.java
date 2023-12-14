package com.sqli.matchmaking.service.composite;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.Field;
import com.sqli.matchmaking.model.Sport;
import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.repository.composite.MatchRepository;

@Service
public class MatchService {
    
    @Autowired
    private MatchRepository matchRepository;

    public void filterPassedMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isAfter(Instant.now()));
    }

    public void filterComingMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isBefore(Instant.now()));
    }

    /* 
     * filtering
     */
    public List<Match> getMatches() {
        List<Match> all = new ArrayList<>(matchRepository.findAll());
        return all;
    }

    public List<Match> getMatchesBySport(Sport sport) {
        List<Match> all = matchRepository.findMatchBySport(sport);
        return all;
    }

    public List<Match> getMatchesByField(Field field) {
        List<Match> all = matchRepository.findMatchByField(field);
        return all;
    }

    public List<Match> getMatchesByDay(Instant date) {
        List<Match> all = matchRepository.findMatchByDay(date);
        all.removeIf(match -> match.getDate().isAfter(Instant.now()));
        return all;
    }

    public boolean isFieldAlreadyBooked(Field field, Instant start, Duration duration) {
        List<Match> set = new ArrayList<>(getMatchesByField(field));
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

    public void deleteById(Long id) {
        matchRepository.deleteById(id);
    }

}
