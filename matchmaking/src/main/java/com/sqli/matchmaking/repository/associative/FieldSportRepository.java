package com.sqli.matchmaking.repository.associative;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sqli.matchmaking.model.associative.FieldSport;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;

@Repository
public interface FieldSportRepository extends JpaRepository<FieldSport, Long> {

    Optional<FieldSport> findByFieldAndSport(Field field, Sport sport);

}
