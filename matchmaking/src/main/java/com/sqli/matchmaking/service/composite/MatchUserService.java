package com.sqli.matchmaking.service.composite;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.composite.MatchRepository;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;

@Service
public class MatchUserService {

    @Autowired
    private MatchUserRepository matchUserRepository;

    @Autowired
    private MatchRepository matchRepository;


    public MatchUserRepository repository() {
        return matchUserRepository;
    }


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

    /* 
     * basic 
     */
    public MatchUser getById(Long id) {
        return matchUserRepository.findById(id).orElse(null);
    }

    /*TODO: think of overiding system service */
    public void save(MatchUser el) {
        // update joined players state in match
        Match match = el.getMatch();
        match.join();
        matchRepository.save(match);
        // save
        matchUserRepository.save(el);
    }

    public void saveAll(List<MatchUser> list) {
        for (MatchUser el : list) 
            this.save(el);
    }

    public void delete(MatchUser el) {
        // update joined players state in match
        Match match = el.getMatch();
        match.unjoin();
        matchRepository.save(match);
        // save
        matchUserRepository.delete(el);
    }


}
