package com.sqli.matchmaking.service.teammaking;

import java.util.List;

import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;


public interface TeamMaking {

    public void make(List<User> players, List<Team> teams);

}
