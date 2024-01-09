package com.sqli.matchmaking.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.request.DTOs;
import com.sqli.matchmaking.service.composite.*;
import com.sqli.matchmaking.service.matchmaking.*;
import com.sqli.matchmaking.service.standalone.*;


@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchUserService matchUserService;
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
    private RandomMaking randomMaking;
    @Autowired
    private ManualMaking manualMaking;

    @PostMapping("/create")
    public ResponseEntity<Object> createMatch(@RequestBody DTOs.Match request) {
        // Check existence of Ids
        User organiser = userService.getById(request.getOrganizerId());
        if (organiser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User does not exist"));
        }
        Field field = fieldService.getById(request.getFieldId());
        if (field == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Field does not exist"));
        }
        Sport sport = sportService.getById(request.getSportId());
        if (sport == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sport does not exist"));
        }
        // check is field does have the sport
        if (!fsService.isSportInField(field, sport)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Selected sport cannot be played in selected field"));
        }
        // check if field is not booked from start to end time
        if (matchService.isFieldAlreadyBooked(field, request.getDate(), request.getDuration())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Field is already booked at that time"));
        }
        // Create Match
        Match el = Match.builder()
                .name(request.getName())
                .organizer(organiser)
                .field(field)
                .sport(sport)
                .date(request.getDate())
                .duration(request.getDuration())
                .noPlayers(request.getNoPlayers())
                .build();
        try {
            // Save match
            matchService.save(el);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Match created successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot save match"));
        }
    }

    @DeleteMapping("uncreate/{id}")
    public ResponseEntity<Object> deleteMatchById(@PathVariable Long id) {
        // Check Id
        Match el = matchService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        try {
            // Delete
            matchService.deleteById(id);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Match deleted successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot delete match"));
        }
    }


    @PostMapping("/join")
    public ResponseEntity<Object> joinMatch(@RequestBody DTOs.MatchUser request) {
        // Check Ids
        User player = userService.getById(request.getUserId());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        Match match = matchService.getById(request.getMatchId());
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Check if there is a place in match for player
        if (matchUserService.ArePlayersFullfilled(match)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sorry, the team is fullfilled"));
        }
        // TODO: check if player is not playing another match in same date

        // Create MatchUser
        MatchUser el = MatchUser.builder()
                    .user(player)
                    .match(match)
                    .build();
        try {
            // Save it
            matchUserService.save(el);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Player joined match successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Maybe player is already in the match."));
        }
    }

    @DeleteMapping("/unjoin")
    public ResponseEntity<Object> deleteMatchUserById(@RequestBody DTOs.MatchUser request) {
        // Check Ids
        User player = userService.getById(request.getUserId());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        Match match = matchService.getById(request.getMatchId());
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Get MatchUser
        MatchUser el = matchUserService.getByMatchAndUser(match, player);
        // Check existence
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player has not joined the match to be out"));
        }
        try {
            // Delete it
            matchUserService.deleteById(el.getId());
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Player unjoined successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot unjoin from match"));
        }
    }


    @GetMapping()
    public ResponseEntity<List<Match>> getMatches(
            @RequestParam("type") String type,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "filter", required = false) String filter) {

        if (!isValidTime(type)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (filter == null && id == null) {
            List<Match> all = matchService.getMatches();
            filterByTime(all, type);
            return ResponseEntity.ok(all);
        }

        if (filter != null && id != null) {
            List<Match> all = null;
            switch (filter) {
                case "sport":
                    Sport sport = sportService.getById(id);
                    if (sport == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    all = matchService.getMatchesBySport(sport);
                    break;
                case "field":
                    Field field = fieldService.getById(id);
                    if (field == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    all = matchService.getMatchesByField(field);
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            filterByTime(all, type);
            return ResponseEntity.ok(all);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/mymatches")
    public ResponseEntity<List<Match>> getUserMatches(
            @RequestParam("type") String type,
            @RequestParam("user") Long userId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "filter", required = false) String filter) {

        if (!isValidTime(type)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.getById(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (filter == null && id == null) {
            List<Match> all = matchUserService.getUserMatches(user);
            filterByTime(all, type);
            return ResponseEntity.ok(all);
        }

        if (filter != null && id != null) {
            List<Match> all = null;
            switch (filter) {
                case "sport":
                    Sport sport = sportService.getById(id);
                    if (sport == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    all = matchUserService.getUserMatchesBySport(user, sport);
                    break;
                case "field":
                    Field field = fieldService.getById(id);
                    if (field == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    all = matchUserService.getUserMatchesByField(user, field);
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            filterByTime(all, type);
            return ResponseEntity.ok(all);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void filterByTime(List<Match> all, String time) {
        switch (time) {
            case "passed":
                matchService.filterPassedMatches(all);
                break;
            case "coming":
                matchService.filterComingMatches(all);
                break;
            default:
        }
    }

    private boolean isValidTime(String time) {
        return Set.of("passed", "coming", "all").contains(time);
    }


    @PostMapping("/make/{id}")
    public ResponseEntity<Object> make(
        @PathVariable Long matchId,
        @RequestParam("how") String how,
        @RequestBody(required = false) DTOs.ManualMaking manualDTO) {

        // Check Id
        Match match = matchService.getById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }

        MatchMaking service = null;
        switch (how) {
            case "random":
                service = this.randomMaking;
                break;
            case "smart":
                service = this.randomMaking;
                break;
            case "manual":
                //! must be required
                service = this.manualMaking;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // Create teams
            service.createTeams(match);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot save team"));
        }

        try {
            // Make the game!
            service.makeJoin(match);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Making has well done!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot make match"));
        }

    }

}

