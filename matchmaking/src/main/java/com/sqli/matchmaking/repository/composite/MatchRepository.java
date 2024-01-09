package com.sqli.matchmaking.repository.composite;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findMatchByName(String name); // filter by name
    List<Match> findMatchBySport(Sport sport); // filter by sport
    List<Match> findMatchByField(Field field); // filter by field
    List<Match> findMatchByDate(Instant date); // filter by instant

    // filter by day
    @Query("SELECT m FROM Match m WHERE DAY(m.date) = DAY(:date)")
    List<Match> findMatchByDay(@Param("date") Instant date);

    // filter by hour
    @Query("SELECT m FROM Match m WHERE HOUR(m.date) = HOUR(:date)")
    List<Match> findMatchByHour(@Param("date") Instant date);

    // filter by day and hour
    @Query("SELECT m FROM Match m WHERE DAY(m.date) = DAY(:date) AND HOUR(m.date) = HOUR(:date)")
    List<Match> findMatchByDayAndHour(@Param("date") Instant date);
    
}
