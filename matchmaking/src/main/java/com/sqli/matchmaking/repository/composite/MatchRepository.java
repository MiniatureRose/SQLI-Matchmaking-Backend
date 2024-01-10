package com.sqli.matchmaking.repository.composite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.composite.Match;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findMatchByName(String name); // filter by name
    
}
