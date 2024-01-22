package com.sqli.matchmaking.controller.standalone;

// utils
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
// dtos
import com.sqli.matchmaking.dtos.*;
// entities
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.service.standalone.UserService;


@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResponseDTOs responseDTOs;
    
    /* 
     * GET
     */
    @GetMapping("all")
    public ResponseEntity<List<ResponseDTOs.UserDetails>> getAllUsers() {
        List<User> all = userService.getAll();
        List<ResponseDTOs.UserDetails> ret = all
            .stream().map(p -> responseDTOs.new UserDetails(p))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ret);
    }

    @GetMapping("id")
    public ResponseEntity<ResponseDTOs.UserDetails> getUserById(
        @RequestParam @NonNull Long id) {
        User el = userService.getById(id);
        return ResponseEntity.ok(responseDTOs.new UserDetails(el));
    }


    /* 
     * PUT
     */
    @PutMapping("setrank")
    @Transactional
    public ResponseEntity<Object> setRank(
        @RequestParam @NonNull Long adminId,
        @RequestParam @NonNull Long playerId,
        @RequestParam @NonNull Integer newRank) {
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