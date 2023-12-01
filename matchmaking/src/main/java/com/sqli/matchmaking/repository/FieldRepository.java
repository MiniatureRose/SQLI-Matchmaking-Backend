package com.sqli.matchmaking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.Field;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    Optional<Field> findByName(String name);
    
}
