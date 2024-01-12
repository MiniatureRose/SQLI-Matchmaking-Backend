package com.sqli.matchmaking.service.composite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.composite.TeamUser;
import com.sqli.matchmaking.repository.composite.TeamRepository;
import com.sqli.matchmaking.repository.composite.TeamUserRepository;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamUserRepository teamUserRepository;

    public TeamRepository repository() {
        return teamRepository;
    }

    
    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    public Team getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public void save(Team el) {
        teamRepository.save(el);
    }

    public void delete(Team el) {
        // Remove all teamusers having el
        List<TeamUser> teamUsers = teamUserRepository.findByTeam(el);
        teamUserRepository.deleteAll(teamUsers);
        // Then remove team
        teamRepository.delete(el);
    }

    public List<Team> getMatchTeams(Match match) {
        return teamRepository.findByMatch(match);
    }

}
