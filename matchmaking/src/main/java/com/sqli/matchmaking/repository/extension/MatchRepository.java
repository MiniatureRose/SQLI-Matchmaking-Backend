package com.sqli.matchmaking.repository.extension;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.extension.Match;

import java.time.Instant;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findMatchByName(String name); 

    List<Match> findByDateBetween(Instant startTime, Instant endTime);

    List<Match> findByStatus(String status);
    
}
