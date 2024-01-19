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
import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
// services
import com.sqli.matchmaking.service.composite.*;


@RestController
@RequestMapping("field")
public class FieldController {

    @Autowired
    private FieldSportService fsService;
    
    /* 
     * GET
     */
    @GetMapping("all")
    public List<Field> getAllFields() {
        return fsService.getAllFields();
    }

    @GetMapping("id")
    public ResponseEntity<Field> getFieldById(@RequestParam Long id) {
        Field el = fsService.getFieldById(id);
        return ResponseEntity.ok(el);
    }

    /* 
     * POST
     */
    @PostMapping("create")
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
     * fieldsport
     */
    @PostMapping("fieldsport")
    public ResponseEntity<Object> createFieldSport(@RequestBody RequestDTOs.FieldSport request) {
        Field field = fsService.getFieldById(request.getFieldId());
        Sport sport = fsService.getSportById(request.getSportId());
        FieldSport el = FieldSport.builder()
                .field(field)
                .sport(sport)
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "FieldSport created successfully!"));
    }
    
}
