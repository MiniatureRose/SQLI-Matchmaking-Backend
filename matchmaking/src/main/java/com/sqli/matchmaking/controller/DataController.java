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
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
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

    @Autowired
    private MatchUserService matchUserService;
    
    /* 
     * user
     */
    @GetMapping("/user/all")
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
    @GetMapping("/field/all")
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
    @GetMapping("/sport/all")
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
    @GetMapping("/match")
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


    @GetMapping("match/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Match el = matchService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
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

    /* 
     * team
     */
    @GetMapping("/team/all")
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

    /* 
     * matchuser
     */
    @GetMapping("/matchuser")
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


    @GetMapping("/matchuser/{id}")
    public ResponseEntity<MatchUser> getMatchUserById(@PathVariable Long id) {
        MatchUser el = matchUserService.getById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        };
        return ResponseEntity.ok(el);
    }

    @DeleteMapping("/matchuser/{id}")
    public ResponseEntity<Object> deleteMatchUserById(@PathVariable Long id) {
        MatchUser el = matchUserService.getById(id);
        if (el == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Team does not exist"));
        }
        matchUserService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Team deleted successfully!"));
    }
    
}
