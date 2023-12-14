package com.sqli.matchmaking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.*;
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.service.*;
import com.sqli.matchmaking.service.composite.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private SportService sportService;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private TeamService teamService;
    
    /* 
     * user
     */
    @GetMapping("/allusers")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User el = userService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        User el = userService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "user does not exist"));
        }
        userService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "User deleted successfully!"));
    }

    /* 
     * field
     */
    @GetMapping("/allfields")
    public List<Field> getAllFields() {
        return fieldService.getAll();
    }

    @GetMapping("field/{id}")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id) {
        Field el = fieldService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @DeleteMapping("field/{id}")
    public ResponseEntity<Object> deleteFieldById(@PathVariable Long id) {
        Field el = fieldService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Field does not exist"));
        }
        fieldService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Field deleted successfully!"));
    }


    /* 
     * sport
     */
    @GetMapping("/allsports")
    public List<Sport> getAllSports() {
        return sportService.getAll();
    }

    @GetMapping("sport/{id}")
    public ResponseEntity<Sport> getSportById(@PathVariable Long id) {
        Sport el = sportService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @DeleteMapping("sport/{id}")
    public ResponseEntity<Object> deleteSportById(@PathVariable Long id) {
        Sport el = sportService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sport does not exist"));
        }
        sportService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Sport deleted successfully!"));
    }

    /* 
     * match
     */
    @GetMapping("/match/all")
    public ResponseEntity<List<Match>> getAllMatchs() {
        return ResponseEntity.ok(matchService.getAll());
    }

    @GetMapping("/match/passed")
    public ResponseEntity<List<Match>> getPassedMatchs() {
        return ResponseEntity.ok(matchService.getPassedMatchs());
    }

    @GetMapping("/match/coming")
    public ResponseEntity<List<Match>> getComingMatchs() {
        return ResponseEntity.ok(matchService.getComingMatchs());
    }

    @GetMapping("/match/passed/sport/{id}")
    public ResponseEntity<List<Match>> filterPassedMatchBySport(@PathVariable Long id) {
        Sport el = sportService.getById(id);
        if (el == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(matchService.FilterPassedMatchsBySport(el));
    }

    @GetMapping("/match/coming/sport/{id}")
    public ResponseEntity<List<Match>> filterComingMatchBySport(@PathVariable Long id) {
        Sport el = sportService.getById(id);
        if (el == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(matchService.FilterComingMatchsBySport(el));
    }

    @GetMapping("/match/passed/field/{id}")
    public ResponseEntity<List<Match>> filterPassedMatchByField(@PathVariable Long id) {
        Field el = fieldService.getById(id);
        if (el == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(matchService.FilterPassedMatchsByField(el));
    }

    @GetMapping("/match/coming/field/{id}")
    public ResponseEntity<List<Match>> filterComingMatchByField(@PathVariable Long id) {
        Field el = fieldService.getById(id);
        if (el == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(matchService.FilterComingMatchsByField(el));
    }

    @GetMapping("match/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Match el = matchService.getById(id);
        return el == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : ResponseEntity.ok(el);
    }
    
    @DeleteMapping("match/{id}")
    public ResponseEntity<Object> deleteMatchById(@PathVariable Long id) {
        Match el = matchService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "match does not exist"));
        }
        matchService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Match deleted successfully!"));
    }


    /* 
     * team
     */
    @GetMapping("/allteams")
    public List<Team> getAllTeams() {
        return teamService.getAll();
    }

    @GetMapping("team/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Team el = teamService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @DeleteMapping("team/{id}")
    public ResponseEntity<Object> deleteTeamById(@PathVariable Long id) {
        Team el = teamService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Team does not exist"));
        }
        teamService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Team deleted successfully!"));
    }
    
}
