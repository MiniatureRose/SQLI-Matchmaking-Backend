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
    
    /* 
     * user
     */
    @GetMapping("/allusers")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @DeleteMapping("user/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
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
    public void deleteMatchById(@PathVariable Long id) {
        matchService.deleteById(id);
    }
}
