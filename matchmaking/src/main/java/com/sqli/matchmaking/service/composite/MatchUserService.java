package com.sqli.matchmaking.service.composite;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;

@Service
public class MatchUserService {

    @Autowired
    private MatchUserRepository matchUserRepository;

    public MatchUser getByMatchAndUser(Match match, User user){
        return matchUserRepository.findByMatchAndUser(match, user);
    }

    /* 
     * user -> match
     */
    public List<Match> getUserMatches(User user) {
        return matchUserRepository.findMatchOfUser(user);
    }

    public List<Match> getUserNoMatches(User user) {
        return matchUserRepository.findMatchOfNoUser(user);
    }

    /* 
     * match -> user
     */
    public List<User> getMatchPlayers(Match match) {
        return matchUserRepository.findUsersByMatch(match);
    }

    public Integer getMatchRemainingPlayers(Match match) {
        Integer res = match.getNoPlayers() - getMatchPlayers(match).size();
        assert res >= 0 : "number of players is out of range";
        return res;
    }

    public boolean ArePlayersFullfilled(Match match) {
        return getMatchRemainingPlayers(match) == 0;
    }

    /* 
     * basic 
     */
    public MatchUser getById(Long id) {
        return matchUserRepository.findById(id).orElse(null);
    }

    public void save(MatchUser el) {
        matchUserRepository.save(el);
    }

    public void deleteById(Long id) {
        matchUserRepository.deleteById(id);
    }


}
