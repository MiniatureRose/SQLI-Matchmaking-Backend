package com.sqli.matchmaking.repository.associative;

import java.util.List;
import java.util.Optional;
import java.time.Instant;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.associative.MatchUser;
import com.sqli.matchmaking.model.extension.Match;
import com.sqli.matchmaking.model.standalone.User;

@Repository
public interface MatchUserRepository extends JpaRepository<MatchUser, Long> {
    
    List<MatchUser> findByUser(User player);

    List<MatchUser> findByMatch(Match match);
    
    @Query("SELECT mu FROM MatchUser mu WHERE mu.match= :match and mu.user= :user")
    Optional<MatchUser> findByMatchAndUser(@Param("match") Match match, @Param("user") User user );

    @Query("SELECT m.match FROM MatchUser m WHERE m.user = :player")
    List<Match> findMatchOfUser(@Param("player") User player);

    @Query("SELECT m FROM Match m WHERE m.id NOT IN (SELECT mu.match.id FROM MatchUser mu WHERE mu.user = :player)")
    List<Match> findMatchOfNoUser(@Param("player") User player);

    @Query("SELECT m.user FROM MatchUser m WHERE m.match = :match")
    List<User> findUsersByMatch(@Param("match") Match match); 

    @Query("SELECT mu.match FROM MatchUser mu WHERE mu.user = :user AND mu.match.date >= :startDateOfNextWeek AND mu.match.date <= :endDateOfNextWeek")
    List<Match> findMatchOfUserForWeek(
        @Param("user") User user, 
        @Param("startDateOfNextWeek") Instant startDateOfNextWeek, 
        @Param("endDateOfNextWeek") Instant endDateOfNextWeek);

}
