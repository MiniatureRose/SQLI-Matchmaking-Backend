package com.sqli.matchmaking.repository.composite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

       Optional<Team> findByName(String name);
       List<Team> findTeamByName(String name); // filter by name
       List<Team> findTeamsByMatch(Match match); //! all teams within a match  reduce ?
       ////List<Match> findMatchByTeam(Team team); //! team match history  reduce 

}
