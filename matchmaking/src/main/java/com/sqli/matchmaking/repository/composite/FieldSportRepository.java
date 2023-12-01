package com.sqli.matchmaking.repository.composite;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.Field;
import com.sqli.matchmaking.model.Sport;
import com.sqli.matchmaking.model.composite.FieldSport;

@Repository
public interface FieldSportRepository extends JpaRepository<FieldSport, Long> {

    Optional<FieldSport> findByFieldAndSport(Field field, Sport sport);

}
