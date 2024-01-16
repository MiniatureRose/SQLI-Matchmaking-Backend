package com.sqli.matchmaking.service.playerranking;

import java.util.List;

import com.sqli.matchmaking.model.composite.Team;


public interface PlayerRanking {

    public void rank(List<Team> teams);

}