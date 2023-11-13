package com.sqli.matchmaking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sqli.matchmaking.Model.Matchs;
// import java.util.Optional;

public interface MatchRepository extends JpaRepository<Matchs, Integer> {
    
}
