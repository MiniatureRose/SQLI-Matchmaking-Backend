package com.sqli.matchmaking.repository.extension;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.extension.Match;
import com.sqli.matchmaking.model.extension.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

       List<Team> findByName(String name); // filter by name
       List<Team> findByMatch(Match match); //! all teams within a match  reduce ?
       ////List<Match> findMatchByTeam(Team team); //! team match history  reduce 

}
