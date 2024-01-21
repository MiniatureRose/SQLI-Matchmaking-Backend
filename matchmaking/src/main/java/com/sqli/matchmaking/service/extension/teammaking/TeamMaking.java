package com.sqli.matchmaking.service.extension.teammaking;

import java.util.List;

import com.sqli.matchmaking.model.extension.Team;
import com.sqli.matchmaking.model.standalone.User;


public interface TeamMaking {

    public void make(List<User> players, List<Team> teams);

}
