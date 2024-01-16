package com.sqli.matchmaking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.dtos.RequestDTOs;
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.service.auth.UserService;
import com.sqli.matchmaking.service.composite.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("data")
public final class DataController {

    @Autowired
    private UserService userService;
    @Autowired
    private FieldSportService fsService;
    
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
        return fsService.getAllFields();
    }

    @GetMapping("field")
    public ResponseEntity<Field> getFieldById(@RequestParam Long id) {
        Field el = fsService.getFieldById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @PostMapping("field")
    public ResponseEntity<Object> createField(@RequestBody RequestDTOs.Field request) {
        Field el = Field.builder()
                .name(request.getName())
                .location(request.getLocation())
                .noPlayers(request.getNoPlayers())
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Field created successfully!"));
    }

    /* 
     * sport
     */
    @GetMapping("sport/all")
    public List<Sport> getAllSports() {
        return fsService.getAllSports();
    }

    @GetMapping("sport")
    public ResponseEntity<Sport> getSportById(@RequestParam Long id) {
        Sport el = fsService.getSportById(id);
        if (el == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(el);
    }

    @PostMapping("sport")
    public ResponseEntity<Object> createSport(@RequestBody RequestDTOs.Sport request) {
        Sport el = Sport.builder()
                .name(request.getName()) // primary key maybe ?
                .noTeams(request.getNoTeams())
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Sport created successfully!"));
    }

    /* 
     * fieldsport
     */
    @PostMapping("fieldsport")
    public ResponseEntity<Object> createFieldSport(@RequestBody RequestDTOs.FieldSport request) {
        Field field = fsService.getFieldById(request.getFieldId());
        Sport sport = fsService.getSportById(request.getSportId());
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
    
}
