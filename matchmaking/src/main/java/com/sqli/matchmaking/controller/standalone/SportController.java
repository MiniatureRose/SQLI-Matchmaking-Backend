package com.sqli.matchmaking.controller.standalone;

// utils
import java.util.List;
import java.util.Map;
// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// dtos
import com.sqli.matchmaking.dtos.*;
// entities
import com.sqli.matchmaking.model.standalone.*;
// services
import com.sqli.matchmaking.service.composite.*;


@RestController
@RequestMapping("sport")
public class SportController {

    @Autowired
    private FieldSportService fsService;

    /* 
     * sport
     */
    @GetMapping("all")
    public List<Sport> getAllSports() {
        return fsService.getAllSports();
    }

    @GetMapping("id")
    public ResponseEntity<Sport> getSportById(@RequestParam Long id) {
        Sport el = fsService.getSportById(id);
        return ResponseEntity.ok(el);
    }

    @PostMapping("create")
    public ResponseEntity<Object> createSport(@RequestBody RequestDTOs.Sport request) {
        Sport el = Sport.builder()
                .name(request.getName()) // primary key maybe ?
                .noTeams(request.getNoTeams())
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "Sport created successfully!"));
    }

}
