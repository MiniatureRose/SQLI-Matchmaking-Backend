package com.sqli.matchmaking.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.Model.Matchs;
import com.sqli.matchmaking.Repository.MatchRepository;

import java.util.List;

@RestController
@RequestMapping("/matchs")
public class MatchController {
    private final MatchRepository matchRepository;
    
    @Autowired
    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping
    public List<Matchs> getAllMatchs() {
        return matchRepository.findAll();
    }

    @GetMapping("/{id}")
    public Matchs getMatchById(@PathVariable Integer id) {
        return matchRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Matchs createMatch(@RequestBody Matchs match) {
        return matchRepository.save(match);
    }

    @PutMapping("/{id}")
    public Matchs updateMatch(@PathVariable Integer id, @RequestBody Matchs match) {
        match.setId(id);
        return matchRepository.save(match);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Integer id) {
        matchRepository.deleteById(id);
    }
}