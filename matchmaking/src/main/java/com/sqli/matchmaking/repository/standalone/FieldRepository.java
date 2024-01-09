package com.sqli.matchmaking.repository.standalone;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.standalone.Field;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    Optional<Field> findByName(String name);
    
}
