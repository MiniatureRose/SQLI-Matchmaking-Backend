package com.sqli.matchmaking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.*;
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.modeljson.FieldCdto;
import com.sqli.matchmaking.modeljson.SportCdto;
import com.sqli.matchmaking.modeljson.UserCdto;
import com.sqli.matchmaking.modeljson.composite.*;
import com.sqli.matchmaking.service.*;
import com.sqli.matchmaking.service.composite.*;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/create")
public class CreateController {

    @Autowired
    private MatchUserService matchUserService;
    @Autowired
    private TeamUserService teamUserService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private FieldSportService fsService;
    @Autowired
    private UserService userService;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private SportService sportService;
    @Autowired
    private TeamService teamService;
    

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody UserCdto request) {
        if (userService.emailAlreadyExists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email already exits"));
        }
        System.out.println("hhhhhh");
        // TODO: Check the structure
        // TODO: send email verification
        User el = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        userService.save(el);
        System.out.println("hhhhgggghh");
        return ResponseEntity.ok().body(Map.of("message", "User signed up successfully!"));
    }


    @PostMapping("/sport")
    public ResponseEntity<Object> createSport(@RequestBody SportCdto request) {
        Sport el = Sport.builder()
                .name(request.getName()) // primary key maybe ?
                .noTeams(request.getNoTeams())
                .build();
        sportService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Sport created successfully!"));
    }

    @PostMapping("/field")
    public ResponseEntity<Object> createTeam(@RequestBody FieldCdto request) {
        Field el = Field.builder()
                .name(request.getName())
                .location(request.getLocation())
                .noPlayers(request.getNoPlayers())
                .build();
        fieldService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Field created successfully!"));
    }

    @PostMapping("/team")
    public ResponseEntity<Object> createTeam(@RequestBody TeamCdto request) {
        Match match = matchService.getById(request.getMatch_id());
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "match does not exist"));
        }
        if (teamService.nameAlreadyExists(request.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Name already exits"));
        } // maybe we can make it primary key
        // max of team is 2
        if (teamService.getMatchTeams(match).size() >= 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sorry, all teams arecreated for this match"));
        }
        Team el = Team.builder()
                .name(request.getName())
                .match(match)
                .build();
        teamService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Team created successfully!"));
    }


    @PostMapping("/fieldsport")
    public ResponseEntity<Object> createFieldSport(@RequestBody FieldSportCdto request) {
        Field field = fieldService.getById(request.getField_id());
        Sport sport = sportService.getById(request.getSport_id());
        if (field == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "field does not exist"));
        }
        if (sport == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "sport does not exist"));
        }
        FieldSport el = FieldSport.builder()
                .field(field)
                .sport(sport)
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "FieldSport created successfully!"));
    }


    @PostMapping("/match")
    public ResponseEntity<Object> createMatch(@RequestBody MatchCdto request) {
        User organiser = userService.getById(request.getOrganizer_id());
        Field field = fieldService.getById(request.getField_id());
        Sport sport = sportService.getById(request.getSport_id());
        if (organiser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "user does not exist"));
        }
        if (field == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "field does not exist"));
        }
        if (sport == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "sport does not exist"));
        }
        // check is field does have the sport
        if (!fsService.isSportInField(field, sport)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "selected sport cannot be played in selected field"));
        }
        // check if field is not booked from start to end time
        if (matchService.isFieldAlreadyBooked(field, request.getDate(), request.getDuration())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "field is already booked at that time"));
        }
        Match el = Match.builder()
                .name(request.getName())
                .organizer(organiser)
                .field(field)
                .sport(sport)
                .date(request.getDate())
                .duration(request.getDuration())
                .noPlayers(request.getNoPlayers())
                .build();
        matchService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Match created successfully!"));
    }


    @PostMapping("/teamuser")
    public ResponseEntity<Object> createTeamUser(@RequestBody TeamUserCdto request) {
        User player = userService.getById(request.getUser_id());
        Team team = teamService.getById(request.getTeam_id());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "match does not exist"));
        }
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "team does not exist"));
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
                    .body(Map.of("error", "some weird error"));
        }
    }

    @PostMapping("/matchuser")
    public ResponseEntity<Object> createMatchUser(@RequestBody MatchUserCdto request) {
        User player = userService.getById(request.getUser_id());
        Match match = matchService.getById(request.getMatch_id());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "match does not exist"));
        }
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "team does not exist"));
        }
        // check if there is a place in match for player
        if (matchUserService.ArePlayersFullfilled(match)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sorry, the team is fullfilled"));
        }
        // TODO: check if player is not playing another match in same date
        try {
            MatchUser el = MatchUser.builder()
                    .user(player)
                    .match(match)
                    .build();
            matchUserService.save(el);
            return ResponseEntity.ok().body(Map.of("message", "Player joined match successfully!"));
        } catch (DataIntegrityViolationException e) {
            // Handle the exception caused by the unique constraint violation
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Player is already in the match."));
        }
    }


}