package com.sqli.matchmaking.repository.composite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.User;
import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;

@Repository
public interface MatchUserRepository extends JpaRepository<MatchUser, Long> {
    
    List<MatchUser> findByUser(User player);
    List<Match> findMatchByUser(User player); // user match history
    List<User> findUsersByMatch(Match match); // all players within a match

}
