package com.sqli.matchmaking.service.composite;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.composite.Team;
import com.sqli.matchmaking.model.composite.TeamUser;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.composite.TeamUserRepository;

@Service
public class TeamUserService {

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Autowired
    private TeamService teamService;


    public TeamUserRepository repository() {
        return teamUserRepository;
    }


    public List<TeamUser> getAll() {
        return teamUserRepository.findAll();
    }

    public TeamUser getById(Long id) {
        return teamUserRepository.findById(id).orElse(null);
    }

    public void save(TeamUser el) {
        teamUserRepository.save(el);
    }

    public void deleteById(Long id) {
        teamUserRepository.deleteById(id);
    }

    public List<User> getMatchPlayersOfTeam(Team team) {
        return teamUserRepository.findUserByTeam(team);
    }

    public List<List<User>> getMatchPlayersByTeam(Match match) {
        List<Team> teams = teamService.getMatchTeams(match);
        return teams.stream().map(t -> teamUserRepository.findUserByTeam(t))
                .collect(Collectors.toList());
    }

}
