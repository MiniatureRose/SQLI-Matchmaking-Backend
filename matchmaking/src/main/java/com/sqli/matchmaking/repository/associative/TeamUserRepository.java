package com.sqli.matchmaking.repository.associative;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.associative.TeamUser;
import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    List<TeamUser> findByTeam(Team team); 

    List<TeamUser> findByUser(User user); 

    @Query("SELECT tu.team FROM TeamUser tu WHERE tu.user = :user")
    List<Team> findTeamsByUser(@Param("user") User user); //! user team history  reduce ?

    @Query("SELECT tu.user FROM TeamUser tu WHERE tu.team = :team")
    List<User> findUsersByTeam(@Param("team")Team team); // players of team within a match

}
