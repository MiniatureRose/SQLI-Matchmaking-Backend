package com.sqli.matchmaking.service.extension.playerranking;

import java.util.List;

import com.sqli.matchmaking.model.extension.Team;


public interface PlayerRanking {

    public void rank(List<Team> teams);

}