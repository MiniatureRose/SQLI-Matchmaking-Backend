package com.sqli.matchmaking.controller;

// utils
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.dtos.*;
// entities
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
// services
import com.sqli.matchmaking.service.auth.UserService;
import com.sqli.matchmaking.service.standalone.NotificationService;
import com.sqli.matchmaking.service.composite.*;
import com.sqli.matchmaking.service.playerranking.*;
import com.sqli.matchmaking.service.teammaking.*;
import com.sqli.matchmaking.service.playerranking.forms.*;
import com.sqli.matchmaking.service.teammaking.forms.*;




@RestController
@RequestMapping("match")
public class MatchController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private FieldSportService fieldSportService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RandomMaking randomMaking;
    @Autowired
    private EvenMaking evenMaking;
    @Autowired
    private DefaultRanking defaultRanking;


    /*
     * POST
     */
    @PostMapping("create")
    public ResponseEntity<Object> createMatch(@RequestBody RequestDTOs.Match request) {
        // Check existence of Ids
        User organiser = userService.getById(request.getOrganizerId());
        Field field = fieldSportService.getFieldById(request.getFieldId());
        Sport sport = fieldSportService.getSportById(request.getSportId());
        // check is field does have the sport
        if (!fieldSportService.isSportInField(field, sport)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Selected sport cannot be played in selected field"));
        }
        // check if field is not booked from start to end time
        if (matchService.isFieldAlreadyBooked(field, request.getDate(), request.getDuration())) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .body(Map.of("error", "Field is already booked at that time"));
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
        // Save match
        matchService.save(el);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match created successfully!"));
    }

    @PostMapping("join")
    public ResponseEntity<Object> joinMatch(@RequestBody RequestDTOs.MatchUser request) {
        // Check Ids
        User player = userService.getById(request.getUserId());
        Match match = matchService.getById(request.getMatchId());
        // Check status
        matchService.assertMatchStatus(match, Match.PENDING, true);
        // Check if there is a place in match for player
        matchService.assertIsFullfiled(match, false);
        // TODO: check if player is not playing another match in same date
        // Create MatchUser
        MatchUser el = MatchUser.builder()
                    .user(player)
                    .match(match)
                    .build();
        // Save it
        matchService.save(el);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Player joined match successfully!"));
    }

    @PostMapping("make/auto")
    @Transactional
    public ResponseEntity<Object> autoMaking(
        @RequestParam Long userId,
        @RequestParam Long matchId,
        @RequestParam String model) {
        // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check match is closed
        matchService.assertMatchStatus(match, Match.CLOSED, true);
        // Define making model
        TeamMaking service = null;
        switch (model) {
            case "random":
                service = this.randomMaking;
                break;
            case "even":
                service = this.evenMaking;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Make match
        matchService.makeTeams(match, service);
        notificationService.SendTeamsCreatedNotifications(match);
        // Change status
        matchService.form(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Making has well done!"));
    }

    @PostMapping("make/manual")
    @Transactional
    public ResponseEntity<Object> manualMaking(
        @RequestParam Long userId,
        @RequestParam Long matchId,
        @RequestBody List<RequestDTOs.TeamPlayers> manualDTO) {
        // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check match is closed
        matchService.assertMatchStatus(match, Match.CLOSED, true);
        // Check number of teams
        if (match.getSport().getNoTeams() != manualDTO.size()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Number of teams is not respected"));
        }
        for (RequestDTOs.TeamPlayers tp : manualDTO) {
            // Create team
            Team team = Team.builder()
                .name(tp.getTeamName())
                .match(match)
                .build();
            // Save it
            teamService.save(team);
            // Join all team players
            for (Long playerId : tp.getPlayersIds()) {
                User player = userService.getById(playerId);
                // Create join
                TeamUser join = TeamUser.builder().team(team).user(player).build();
                // Save it
                teamService.save(join);
            }
        }
        // Change status
        matchService.form(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Manual Making has well done!"));
    }

    @PostMapping("record")
    @Transactional
    public ResponseEntity<Object> record(
        @RequestParam Long recorderId,
        @RequestParam String model,
        @RequestBody List<RequestDTOs.TeamRecord> records) {
        // Check Ids
        User user = userService.getById(recorderId);
        // Check records
        if (records.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(Map.of("message", "No record given"));
        }
        // Get the match
        Match match = teamService.getById(records.get(0).getTeamId()).getMatch();
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check match is confirmed
        matchService.assertMatchStatus(match, Match.CONFIRMED, true);
        // Check match is played
        matchService.assertIsPassed(match, true);

        // Set teams scores
        for (RequestDTOs.TeamRecord record : records) {
            Team team = teamService.getById(record.getTeamId());
            // Check reference
            if (!team.getMatch().equals(match)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "All teams must refer to one match"));
            }
            // Set score
            teamService.updateScore(team, record.getScore());
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
        // Rank players
        teamService.rankPlayers(match, service);
        // Change status 
        matchService.record(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match recorded successfully!"));
    }


    /*
     * PUT
     */
    @PutMapping("setrank")
    @Transactional
    public ResponseEntity<Object> setRank(
        @RequestParam Long adminId,
        @RequestParam Long playerId,
        @RequestParam Double newRank) {
        // Check ids
        User admin = userService.getById(adminId);
        User player = userService.getById(playerId);
        // Check authorities
        userService.onlyAdmin(admin);
        // Set the rank 
        userService.updateRank(player, newRank);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Rank updated successfully!"));
    }
    
    @PutMapping("confirm")
    @Transactional
    public ResponseEntity<Object> confirm(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
       // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check match is formed
        matchService.assertMatchStatus(match, Match.FORMED, true);
        // Change status
        matchService.confirm(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match confirmed successfully!"));
    }

    @PutMapping("close")
    @Transactional
    public ResponseEntity<Object> close(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
        // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check match is pending
        matchService.assertMatchStatus(match, Match.PENDING, true);
        // Check if match is fullfield
        matchService.assertIsFullfiled(match, true);
        // Change status
        matchService.close(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match closed successfully!"));
    }

    @PutMapping("cancel")
    @Transactional
    public ResponseEntity<Object> cancel(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
       // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check status and cancel
        matchService.cancel(match);
        // Send notification
        notificationService.SendCanceledMatchNotifications(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match canceled successfully!"));
    }

    @PutMapping("pend")
    @Transactional
    public ResponseEntity<Object> pend(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
        // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Check match is not pending
        matchService.assertMatchStatus(match, Match.PENDING, false);
        // Delete teams
        teamService.deleteMatchTeams(match);
        // Change status
        matchService.pend(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match is back pending successfully!"));
    }

    

    /*
     * DELETE
     */
    @DeleteMapping("uncreate")
    @Transactional
    public ResponseEntity<Object> deleteMatch(        
        @RequestParam Long userId,
        @RequestParam Long matchId) {
        // Check Ids
        User user = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check authorities
        userService.onlyOrganizerAndAdmin(user, match);
        // Delete
        matchService.delete(match);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Match deleted successfully!"));
    }

    @DeleteMapping("unjoin")
    @Transactional
    public ResponseEntity<Object> deleteMatchUser(
        @RequestParam Long userId,
        @RequestParam Long matchId) {
        // Check Ids
        User player = userService.getById(userId);
        Match match = matchService.getById(matchId);
        // Check status
        matchService.assertMatchStatus(match, Match.PENDING, true);
        // Get MatchUser
        MatchUser el = matchService.getByMatchAndUser(match, player);
        // Delete it
        matchService.delete(el);
        // Confirm
        return ResponseEntity.ok().body(Map.of("message", "Player unjoined successfully!"));
    }



    /*
     * GET
     */
    @GetMapping("players")
    public ResponseEntity<List<ResponseDTOs.UserDetails>> getMatchPlayers(
        @RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        // Return
        List<ResponseDTOs.UserDetails> ret = matchService.getMatchPlayers(match)
            .stream().map(p -> new ResponseDTOs.UserDetails(p))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ret);
    }

    @GetMapping("teams")
    public ResponseEntity<List<ResponseDTOs.TeamDetails>> getMatchTeams(
        @RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        // Return
        List<Team> teams = teamService.getMatchTeams(match);
        List<ResponseDTOs.TeamDetails> ret = teams
            .stream().map(t -> new ResponseDTOs.TeamDetails(teamService.getTeamPlayers(t), t))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ret);
    }

    @GetMapping("id")
    public ResponseEntity<Match> getMatchById(@RequestParam Long matchId) {
        // Check id
        Match match = matchService.getById(matchId);
        // Set current players
        matchService.setCureentPlayers(match);
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
        }
        else if (userId != null && myMatches != null) {
            User user = userService.getById(userId);
            if (myMatches)
                all = matchService.getUserMatches(user);
            else 
                all = matchService.getUserNoMatches(user);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (filter != null && id != null) {
            switch (filter) {
                case "sport":
                    Sport sport = fieldSportService.getSportById(id);
                    matchService.filterMatchesBySport(all, sport);
                    break;
                case "field":
                    Field field = fieldSportService.getFieldById(id);
                    matchService.filterMatchesByField(all, field);
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        else if (filter != null || id != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Filter by time
        filterByTime(all, type);
        // Set current players
        matchService.setCureentPlayers(all);
        // Return
        return ResponseEntity.ok(all);

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

