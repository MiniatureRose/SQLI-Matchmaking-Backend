package com.sqli.matchmaking.repository.standalone;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.standalone.Sport;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    
    Optional<Sport> findByName(String name);
}
