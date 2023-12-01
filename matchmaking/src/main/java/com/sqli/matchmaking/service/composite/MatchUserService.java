package com.sqli.matchmaking.service.composite;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.User;
import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;

@Service
public class MatchUserService {

    @Autowired
    private MatchUserRepository mtpRepository;


    /* 
     * user
     */
    private List<Match> getUserMatchs(User user) {
        return mtpRepository.findMatchByUser(user);
    }

    public List<Match> getUserPassedMatchs(User user) {
        List<Match> all = getUserMatchs(user);
        all.removeIf(match -> match.getDate().isAfter(Instant.now()));
        return all;
    }
    
    public List<Match> getUserComingMatchs(User user) {
        List<Match> all = getUserMatchs(user);
        all.removeIf(match -> match.getDate().isBefore(Instant.now()));
        return all;
    }

    /* 
     * match
     */

    public List<User> getMatchPlayers(Match match) {
        return mtpRepository.findUsersByMatch(match);
    }

    public Integer getMatchRemainingPlayers(Match match) {
        Integer res = match.getNoPlayers() - getMatchPlayers(match).size();
        assert res >= 0 : "number of players is out of range";
        return res;
    }

    public boolean ArePlayersFullfilled(Match match) {
        return getMatchRemainingPlayers(match) == 0;
    }


    public List<MatchUser> getAll() {
        return mtpRepository.findAll();
    }

    public MatchUser getById(Long id) {
        return mtpRepository.findById(id).orElse(null);
    }

    public void save(MatchUser el) {
        mtpRepository.save(el);
    }

    public void deleteById(Long id) {
        mtpRepository.deleteById(id);
    }


}
