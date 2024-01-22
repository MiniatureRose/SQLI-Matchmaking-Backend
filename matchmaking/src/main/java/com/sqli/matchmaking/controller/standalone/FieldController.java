package com.sqli.matchmaking.controller.standalone;

// utils
import java.util.List;
import java.util.Map;
// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
// dtos
import com.sqli.matchmaking.dtos.*;
import com.sqli.matchmaking.model.associative.FieldSport;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.service.standalone.*;


@RestController
@RequestMapping("field")
public class FieldController {

    @Autowired
    private FieldService fsService;

    @Autowired
    private SportService sportService;
    
    /* 
     * GET
     */
    @GetMapping("all")
    public List<Field> getAllFields() {
        return fsService.getAll();
    }

    @GetMapping("id")
    public ResponseEntity<Field> getFieldById(@RequestParam @NonNull Long id) {
        Field el = fsService.getById(id);
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
        Field field = fsService.getById(request.getFieldId());
        Sport sport = sportService.getById(request.getSportId());
        FieldSport el = FieldSport.builder()
                .field(field)
                .sport(sport)
                .build();
        fsService.save(el);
        return ResponseEntity.ok().body(Map.of("message", "FieldSport created successfully!"));
    }
    
}
