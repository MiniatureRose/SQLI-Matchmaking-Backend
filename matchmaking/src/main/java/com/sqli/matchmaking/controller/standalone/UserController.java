package com.sqli.matchmaking.controller.standalone;

// utils
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
// dtos
import com.sqli.matchmaking.dtos.*;
// entities
import com.sqli.matchmaking.model.standalone.*;
// services
import com.sqli.matchmaking.service.auth.UserService;


@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;
    
    /* 
     * GET
     */
    @GetMapping("all")
    public ResponseEntity<List<ResponseDTOs.UserDetails>> getAllUsers() {
        List<User> all = userService.getAll();
        List<ResponseDTOs.UserDetails> ret = all
            .stream().map(p -> new ResponseDTOs.UserDetails(p))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ret);
    }

    @GetMapping("id")
    public ResponseEntity<ResponseDTOs.UserDetails> getUserById(@RequestParam Long id) {
        User el = userService.getById(id);
        return ResponseEntity.ok(new ResponseDTOs.UserDetails(el));
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

}