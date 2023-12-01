package com.sqli.matchmaking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.Sport;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    
    Optional<Sport> findByName(String name);
}
