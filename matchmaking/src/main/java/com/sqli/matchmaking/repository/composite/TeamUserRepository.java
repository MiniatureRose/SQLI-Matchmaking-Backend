package com.sqli.matchmaking.repository.composite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.composite.TeamUser;
import com.sqli.matchmaking.model.standalone.User;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    List<TeamUser> findByTeam(Team team); 
    List<TeamUser> findByUser(User user); 
    List<Team> findTeamByUser(User user); //! user team history  reduce ?
    List<User> findUserByTeam(Team team); // players of team within a match

}
