package com.sqli.matchmaking.repository.composite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.*;
import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;

@Repository
public interface MatchUserRepository extends JpaRepository<MatchUser, Long> {
    
    List<MatchUser> findByUser(User player);

    @Query("SELECT m.match FROM MatchUser m WHERE m.user = :player")
    List<Match> findMatchByUser(@Param("player") User player);

    @Query(value = "SELECT m FROM Match m " +
               "JOIN MatchUser mu ON m.id = mu.match.id " +
               "WHERE mu.user = :player AND m.sport = :sport")
    List<Match> findMatchByUserAndSport(User player, Sport sport); 

    @Query(value = "SELECT m FROM Match m " +
               "JOIN MatchUser mu ON m.id = mu.match.id " +
               "WHERE mu.user = :player AND m.field = :field")
    List<Match> findMatchByUserAndField(User player, Field field);

    @Query("SELECT m.user FROM MatchUser m WHERE m.match = :match")
    List<User> findUsersByMatch(Match match); // all players within a match

}
