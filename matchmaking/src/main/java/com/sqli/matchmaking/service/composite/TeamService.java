package com.sqli.matchmaking.service.composite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.repository.composite.TeamRepository;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    public Team getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public void save(Team el) {
        teamRepository.save(el);
    }

    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

    public boolean nameAlreadyExists(String name) {
        Team find = teamRepository.findByName(name).orElse(null);
        return find == null;
    }

    public List<Team> getMatchTeams(Match match) {
        return teamRepository.findTeamsByMatch(match);
    }

}
