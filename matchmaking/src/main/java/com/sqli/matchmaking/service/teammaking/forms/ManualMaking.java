package com.sqli.matchmaking.service.teammaking.forms;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sqli.matchmaking.dtos.RequestDTOs;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.teammaking.TeamMaking;

@Service
public class ManualMaking implements TeamMaking {

    private RequestDTOs.ManualMaking info;

    public void setInfo(RequestDTOs.ManualMaking info) {
        this.info = info;
    }
    
    public void make(List<User> players, List<Team> teams) {
        System.out.println(info);
    }
    
}
