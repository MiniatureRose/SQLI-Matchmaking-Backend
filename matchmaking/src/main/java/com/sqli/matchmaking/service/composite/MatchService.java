package com.sqli.matchmaking.service.composite;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.repository.composite.MatchRepository;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;
import com.sqli.matchmaking.service.teammaking.TeamMaking;
import com.sqli.matchmaking.service.standalone.NotificationService;

import lombok.Getter;

@Service
@Getter
public class MatchService {
    
    @Autowired
    private TeamService teamService;

    /* 
     * Repository
     */
    @Autowired
    private MatchRepository repository;
    @Autowired
    private MatchUserRepository matchUserRepository;
    

    /* 
     * Making
     */
    public void makeTeams(Match match, TeamMaking service) {
        // Create teams
        teamService.createTeams(match);
        // Get teams for this match
        List<Team> teams = teamService.getMatchTeams(match);
        // Get all match player
        List<User> players = this.getMatchPlayers(match);
        // Make the game!
        service.make(players, teams);
    }

    public void setCureentPlayers(Match match) {
        match.setCurPlayers(this.getMatchPlayers(match).size());
    }

    public void setCureentPlayers(List<Match> matches) {
        for (Match match : matches) this.setCureentPlayers(match);
    }


    /* 
     * Booleans
     */
    public Boolean isFullfilled(Match match) {
        int curPlayers =  this.getMatchPlayers(match).size();
        return curPlayers == match.getNoPlayers();
    }

    public boolean isFieldAlreadyBooked(Field field, Instant start, Duration duration) {
        List<Match> all = this.getAll();
        this.filterMatchesByField(all, field);
        filterComingMatches(all);
        Instant end = start.plus(duration);
        return all.removeIf(match -> {
                var startMatch = match.getDate();
                var endMatch = match.getDate().plus(match.getDuration());
                return (endMatch.isAfter(start) && startMatch.isBefore(end));
            });
    }

    /* 
     * Assertions
     */
    public void assertMatchStatus(Match match, String status, Boolean bool) {
        if (match.getStatus().equals(status) != bool) {
            throw new Exceptions.MatchMustBeOnStatus(status, bool);
        }
    }

    public void assertIsPassed(Match match, Boolean bool) {
        if (match.isPassed() != bool) {
            throw new Exceptions.MatchMustBeOnStatus("passed", bool);
        }
    }

    public void assertIsFullfiled(Match match, Boolean bool) {
        if (this.isFullfilled(match) != bool) {
            throw new Exceptions.MatchMustBeOnStatus("fullfilled", bool);
        }
    }


    /* 
     * Filtering
     */
    public void filterMatchesBySport(List<Match> matches, Sport sport) {
        matches.removeIf(match -> !match.getSport().equals(sport));
    }

    public void filterMatchesByField(List<Match> matches, Field field) {
        matches.removeIf(match -> !match.getField().equals(field));       
    }

    public void filterPassedMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isAfter(Instant.now()));
    }

    public void filterComingMatches(List<Match> all) {
        all.removeIf(match -> match.getDate().isBefore(Instant.now()));
    }

    public List<User> getMatchPlayers(Match match) {
        return matchUserRepository.findUsersByMatch(match);
    }

    public List<Match> getUserMatches(User user) {
        return matchUserRepository.findMatchOfUser(user);
    }

    public List<Match> getUserNoMatches(User user) {
        return matchUserRepository.findMatchOfNoUser(user);
    }

    public MatchUser getByMatchAndUser(Match match, User user){
        return matchUserRepository.findByMatchAndUser(match, user)
            .orElseThrow(() -> 
                new Exceptions.TwoEntitiesLinkNotFound(
                    "Match", "User", match.getId(), user.getId())
            );
    }


    /* 
     * Basic 
     */
    public List<Match> getAll() {
        return repository.findAll();
    }

    public Match getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> 
                new Exceptions.EntityNotFound("Match", "id", id));
    }

    public void save(Match el) {
        try {
            repository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Match");
        }
    }

    public void save(MatchUser el) {
        try {
            matchUserRepository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("MatchUser");
        }
    }

    @Transactional
    public void pend(Match el) {
        try {
            el.setStatus(Match.PENDING);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Match", "status -> pending");
        }
        // Save it
        this.save(el);
    }

    @Transactional
    public void confirm(Match el) {
        try {
            el.setStatus(Match.CONFIRMED);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Match", "status -> confirmed");
        }
        // Save it
        this.save(el);
    }

    @Transactional
    public void cancel(Match el) {
        try {
            el.setStatus(Match.CANCELED);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Match", "status -> canceled");
        }
        // Save it
        this.save(el);
    }

    @Transactional
    public void record(Match el) {
        try {
            el.setStatus(Match.RECORDED);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Match", "status -> recorded");
        }
        // Save it
        this.save(el);
    }

    @Transactional
    public void close(Match el) {
        try {
            el.setStatus(Match.CLOSED);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Match", "status -> close");
        }
        // Save it
        this.save(el);
    }

    @Transactional
    public void form(Match el) {
        try {
            el.setStatus(Match.FORMED);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Match", "status -> form");
        }
        // Save it
        this.save(el);
    }
    

    @Transactional
    public void delete(Match el) {
        // Remove all matchUsers having el
        List<MatchUser> matchUsers = matchUserRepository.findByMatch(el);
        try {
            matchUserRepository.deleteAll(matchUsers);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeDeleted("All MatchUsers");
        }
        // Select all teams having el
        List<Team> teams = teamService.getMatchTeams(el);
        for (Team team : teams) {
            try {
                teamService.delete(team);
            } catch (DataIntegrityViolationException e) {
                throw new Exceptions.EntityCannotBeDeleted("Team");
            }
        }
        // Then remove el
        try {
            repository.delete(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeDeleted("Match");
        }
    }

    public void delete(MatchUser el) {
        try {
            matchUserRepository.delete(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeDeleted("MatchUser");
        }
    }

}
