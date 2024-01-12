package com.sqli.matchmaking.repository.composite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.MatchUser;
import com.sqli.matchmaking.model.standalone.User;

@Repository
public interface MatchUserRepository extends JpaRepository<MatchUser, Long> {
    
    List<MatchUser> findByUser(User player);

    List<MatchUser> findByMatch(Match match);
    
    @Query("SELECT mu FROM MatchUser mu WHERE mu.match= :match and mu.user= :user")
    MatchUser findByMatchAndUser(@Param("match") Match match, @Param("user") User user );

    @Query("SELECT m.match FROM MatchUser m WHERE m.user = :player")
    List<Match> findMatchOfUser(@Param("player") User player);

    @Query("SELECT m FROM Match m WHERE m.id NOT IN (SELECT mu.match.id FROM MatchUser mu WHERE mu.user = :player)")
    List<Match> findMatchOfNoUser(@Param("player") User player);

    @Query("SELECT m.user FROM MatchUser m WHERE m.match = :match")
    List<User> findUsersByMatch(@Param("match") Match match); // all players within a match

}
