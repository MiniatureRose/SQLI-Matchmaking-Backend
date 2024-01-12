package com.sqli.matchmaking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.request.DTOs;
import com.sqli.matchmaking.service.composite.*;
import com.sqli.matchmaking.service.standalone.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("data")
public class DataController {

    @Autowired
    private UserService userService;
    @Autowired
    private SportService sportService;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private FieldSportService fsService;
    @Autowired
    private TeamUserService teamUserService;
    
    /* 
     * user
     */
    @GetMapping("user/all")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("user")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        User el = userService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    /* 
     * field
     */
    @GetMapping("field/all")
    public List<Field> getAllFields() {
        return fieldService.getAll();
    }

    @GetMapping("field")
    public ResponseEntity<Field> getFieldById(@RequestParam Long id) {
        Field el = fieldService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @PostMapping("field")
    public ResponseEntity<Object> createField(@RequestBody DTOs.Field request) {
        Field el = Field.builder()
                .name(request.getName())
                .location(request.getLocation())
                .noPlayers(request.getNoPlayers())
                .build();
        fieldService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Field created successfully!"));
    }

    /* 
     * sport
     */
    @GetMapping("sport/all")
    public List<Sport> getAllSports() {
        return sportService.getAll();
    }

    @GetMapping("sport")
    public ResponseEntity<Sport> getSportById(@RequestParam Long id) {
        Sport el = sportService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @PostMapping("sport")
    public ResponseEntity<Object> createSport(@RequestBody DTOs.Sport request) {
        Sport el = Sport.builder()
                .name(request.getName()) // primary key maybe ?
                .noTeams(request.getNoTeams())
                .build();
        sportService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Sport created successfully!"));
    }

    /* 
     * team
     */
    @GetMapping("/team/all")
    public List<Team> getAllTeams() {
        return teamService.getAll();
    }

    @GetMapping("team")
    public ResponseEntity<Team> getTeamById(@RequestParam Long id) {
        Team el = teamService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    /* 
     * fieldsport
     */
    @PostMapping("fieldsport")
    public ResponseEntity<Object> createFieldSport(@RequestBody DTOs.FieldSport request) {
        Field field = fieldService.getById(request.getFieldId());
        Sport sport = sportService.getById(request.getSportId());
        if (field == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Field does not exist"));
        }
        if (sport == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sport does not exist"));
        }
        FieldSport el = FieldSport.builder()
                .field(field)
                .sport(sport)
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "FieldSport created successfully!"));
    }

    /* 
     * teamuser
     */
    @PostMapping("teamuser")
    public ResponseEntity<Object> createTeamUser(@RequestBody DTOs.TeamUser request) {
        User player = userService.getById(request.getUserId());
        Team team = teamService.getById(request.getTeamId());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Team does not exist"));
        }
        try {
            TeamUser el = TeamUser.builder()
                    .user(player)
                    .team(team)
                    .build();
            teamUserService.save(el);
            return ResponseEntity.ok().body(Map.of("message", "Player joined team successfully!"));
        } catch (DataIntegrityViolationException e) {
            // Handle the exception caused by the unique constraint violation
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Some weird error"));
        }
    }

    
}
