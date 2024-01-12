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
@RequestMapping("match")
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

    /*
     * POST
     */
    @PostMapping("create")
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

    @PostMapping("join")
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
        if (match.isFullfilled()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match is fullfilled"));
        }
        // Check if there match is confirmed
        if (match.isConfirmed()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match is confirmed"));
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

    @PostMapping("make")
    public ResponseEntity<Object> make(
        @RequestParam Long userId,
        @RequestParam Long matchId,
        @RequestParam String how,
        @RequestBody(required = false) DTOs.ManualMaking manualDTO) {
        // Check Ids
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        Match match = matchService.getById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Check authorities
        if (!match.getOrganizer().equals(user) && !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "User is neither the organizer or an admin"));
        }
        // Define how
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

    @PostMapping("confirm")
    public ResponseEntity<Object> confirm(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
       // Check Ids
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        Match match = matchService.getById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Check authorities
        if (!match.getOrganizer().equals(user) && !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "User is neither the organizer or an admin"));
        }
        // Check if match is fullfield
        if (match.isFullfilled() == false) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match is not fullfilled to be confirmed"));
        }
        try {
            // Confirm
            match.confirm();
            matchService.save(match);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Match confirmed successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot confirm match"));
        }
    }

    @PostMapping("cancel")
    public ResponseEntity<Object> cancel(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
       // Check Ids
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        Match match = matchService.getById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Check authorities
        if (!match.getOrganizer().equals(user) && !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "User is neither the organizer or an admin"));
        }
        try {
            // Cancel
            match.cancel();
            matchService.save(match);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Match canceled successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot cancel match"));
        }
    }


    /*
     * DELETE
     */
    @DeleteMapping("uncreate")
    public ResponseEntity<Object> deleteMatch(        
        @RequestParam Long userId,
        @RequestParam Long matchId) {
        // Check Ids
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User does not exist"));
        }
        Match match = matchService.getById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Check authorities
        if (!match.getOrganizer().equals(user) && !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "User is neither the organizer or an admin"));
        }
        try {
            // Delete
            matchService.delete(match);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Match deleted successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot delete match"));
        }
    }

    @DeleteMapping("unjoin")
    public ResponseEntity<Object> deleteMatchUser(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
        // Check Ids
        User player = userService.getById(userId);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        Match match = matchService.getById(matchId);
        if (match == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match does not exist"));
        }
        // Check if there match is confirmed
        if (match.isConfirmed()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match is confirmed"));
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
            matchUserService.delete(el);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Player unjoined successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot unjoin from match"));
        }
    }


    /*
     * GET
     */
    @GetMapping("players")
    public ResponseEntity<List<User>> getMatchPlayers(@RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return
        return ResponseEntity.ok(matchUserService.getMatchPlayers(match));
    }

    
    @GetMapping("id")
    public ResponseEntity<Match> getMatchById(@RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return
        return ResponseEntity.ok(match);
    }


    @GetMapping("")
    public ResponseEntity<List<Match>> geMatches(
            @RequestParam String type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean myMatches,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String filter) {

        if (!isValidTime(type)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Match> all = null;
        if (userId == null && myMatches == null) {
            all = matchService.getMatches();
            filterByTime(all, type);
        }
        else if (userId != null && myMatches != null) {
            User user = userService.getById(userId);
            if (user == null) 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            if (myMatches)
                all = matchUserService.getUserMatches(user);
            else 
                all = matchUserService.getUserNoMatches(user);
            filterByTime(all, type);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (filter == null && id == null) {
            return ResponseEntity.ok(all);
        }
        else if (filter != null && id != null) {
            switch (filter) {
                case "sport":
                    Sport sport = sportService.getById(id);
                    if (sport == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    all = matchService.getMatchesBySport(all, sport);
                    break;
                case "field":
                    Field field = fieldService.getById(id);
                    if (field == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    all = matchService.getMatchesByField(all, field);
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            filterByTime(all, type);
            return ResponseEntity.ok(all);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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

}

