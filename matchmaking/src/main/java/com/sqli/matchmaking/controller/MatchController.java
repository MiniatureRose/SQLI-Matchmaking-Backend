package com.sqli.matchmaking.controller;

// utils
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.dtos.RequestDTOs;
import com.sqli.matchmaking.dtos.ResponseDTOs;
import com.sqli.matchmaking.dtos.ResponseDTOs.TeamDetails;
import com.sqli.matchmaking.dtos.ResponseDTOs.UserDetails;
// entities
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
// services
import com.sqli.matchmaking.service.auth.UserService;
import com.sqli.matchmaking.service.composite.*;
import com.sqli.matchmaking.service.playerranking.*;
import com.sqli.matchmaking.service.playerranking.forms.DefaultRanking;
import com.sqli.matchmaking.service.teammaking.*;
import com.sqli.matchmaking.service.teammaking.forms.ManualMaking;
import com.sqli.matchmaking.service.teammaking.forms.RandomMaking;



@RestController
@RequestMapping("match")
public final class MatchController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private FieldSportService fieldSportService;
    @Autowired
    private UserService userService;
    @Autowired
    private RandomMaking randomMaking;
    @Autowired
    private ManualMaking manualMaking;
    @Autowired
    private DefaultRanking defaultRanking;


    /*
     * POST
     */
    @PostMapping("create")
    public ResponseEntity<Object> createMatch(@RequestBody RequestDTOs.Match request) {
        // Check existence of Ids
        User organiser = userService.getById(request.getOrganizerId());
        if (organiser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User does not exist"));
        }
        Field field = fieldSportService.getFieldById(request.getFieldId());
        if (field == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Field does not exist"));
        }
        Sport sport = fieldSportService.getSportById(request.getSportId());
        if (sport == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Sport does not exist"));
        }
        // check is field does have the sport
        if (!fieldSportService.isSportInField(field, sport)) {
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
    public ResponseEntity<Object> joinMatch(@RequestBody RequestDTOs.MatchUser request) {
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
        // Check status
        if (!match.isPending()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is either canceled or confirmed"));
        }
        // Check if there is a place in match for player
        if (matchService.isFullfilled(match)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Match is fullfilled"));
        }
        // TODO: check if player is not playing another match in same date
        // Create MatchUser
        MatchUser el = MatchUser.builder()
                    .user(player)
                    .match(match)
                    .build();
        try {
            // Save it
            matchService.save(el);
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
        @RequestParam String model,
        @RequestBody(required = false) RequestDTOs.ManualMaking manualDTO) {
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
        // Check status
        if (!match.isPending()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is either canceled or confirmed"));
        }
        // Define making model
        TeamMaking service = null;
        switch (model) {
            case "random":
                service = this.randomMaking;
                break;
            case "smart":
                service = this.randomMaking;
                break;
            case "manual":
                //! dto must be required
                service = this.manualMaking;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // Create teams
            teamService.createTeams(match);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot save team"));
        }

        try {
            // Make match
            matchService.makeTeams(match, service);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Making has well done!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot make match"));
        }

    }

    @PostMapping("record")
    public ResponseEntity<Object> record(
        @RequestParam Long recorderId,
        @RequestParam String model,
        @RequestBody List<RequestDTOs.TeamRecord> records) {
        // Check Ids
        User user = userService.getById(recorderId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        // Check records
        if (records.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "No record given"));
        }
        // Get the match
        Match match = teamService.getById(records.get(0).getTeamId()).getMatch();
        assert match != null : "WEIRD : a stored team with match = null";
        // Check authorities
        if (!match.getOrganizer().equals(user) && !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "User is neither the organizer or an admin"));
        }
        // Check status
        if (!match.isConfirmed()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is not confirmed yet"));
        }
        // Check if match is played
       if (!match.isPassed()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is played or finished yet"));
        }
        // Check that All teams exist and refer to one match
        for (RequestDTOs.TeamRecord record : records) {
            // Check id
            Team team = teamService.getById(record.getTeamId());
            if (team == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Team does not exist"));
            }
            // Check reference
            if (!team.getMatch().equals(match)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "All teams must refer to one match"));
            }
        }
        for (RequestDTOs.TeamRecord record : records) {
            Team team = teamService.getById(record.getTeamId());
            try {
                // Set score
                team.setScore(record.getScore());
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "WEIRD : Cannot set a team score"));
            }
        }
        // Define ranking model
        PlayerRanking service = null;
        switch (model) {
            case "default":
                service = this.defaultRanking;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // Rank players
            teamService.rankPlayers(match, service);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot rank player"));
        }
        try {
            // Record
            match.record();
            matchService.save(match);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Match recorded successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot set match as recorded"));
        }
    }


    /*
     * PUT
     */
    @PutMapping("setrank")
    public ResponseEntity<Object> setRank(
        @RequestParam Long adminId,
        @RequestParam Long playerId,
        @RequestParam Double newRank) {
        // Check ids
        User admin = userService.getById(adminId);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Setter does not exist"));
        }
        User player = userService.getById(playerId);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player does not exist"));
        }
        // Check authorities
        if (!admin.isAdmin()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Setter is neither not an admin"));
        }
        try {
            // Set the rank
            player.setRank(newRank);
            // Confirm
            return ResponseEntity.ok().body(Map.of("message", "Rank updated successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "WEIRD : Cannot set rank"));
        }
    }
    
    @PutMapping("confirm")
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
        // Check status
        if (!match.isPending()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is either canceled or already confirmed"));
        }
        // Check if match is fullfield
        if (matchService.isFullfilled(match) == false) {
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

    @PutMapping("cancel")
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
        // Check status
        if (!match.isPending()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is either already canceled or confirmed"));
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
        // Check status
        if (!match.isPending()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Match is either canceled or confirmed"));
        }
        // Get MatchUser
        MatchUser el = matchService.getByMatchAndUser(match, player);
        // Check existence
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Player has not joined the match to be out"));
        }
        try {
            // Delete it
            matchService.delete(el);
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
    public ResponseEntity<List<ResponseDTOs.UserDetails>> getMatchPlayers(@RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return
        List<ResponseDTOs.UserDetails> ret = matchService.getMatchPlayers(match)
            .stream().map(p -> new UserDetails(p)).collect(Collectors.toList());
        return ResponseEntity.ok(ret);
    }

    @GetMapping("teams")
    public ResponseEntity<List<TeamDetails>> getMatchTeams(@RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return
        List<Team> teams = teamService.getMatchTeams(match);
        List<TeamDetails> ret = teams.stream().map(t -> new TeamDetails(teamService.getTeamPlayers(t), t))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ret);
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
            all = matchService.getAll();
            filterByTime(all, type);
        }
        else if (userId != null && myMatches != null) {
            User user = userService.getById(userId);
            if (user == null) 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            if (myMatches)
                all = matchService.getUserMatches(user);
            else 
                all = matchService.getUserNoMatches(user);
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
                    Sport sport = fieldSportService.getSportById(id);
                    if (sport == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    matchService.filterMatchesBySport(all, sport);
                    break;
                case "field":
                    Field field = fieldSportService.getFieldById(id);
                    if (field == null) 
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    matchService.filterMatchesByField(all, field);
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

