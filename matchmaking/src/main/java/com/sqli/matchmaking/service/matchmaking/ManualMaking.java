package com.sqli.matchmaking.service.matchmaking;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.request.DTOs;

@Service
public class ManualMaking extends MatchMaking {

    private DTOs.ManualMaking info;

    public void setInfo(DTOs.ManualMaking info) {
        this.info = info;
    }
    
    public void make(List<User> players, List<Team> teams) {
        System.out.println(info);
    }
    
}
