package com.sqli.matchmaking.service.composite;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.FieldSport;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.composite.FieldSportRepository;
import com.sqli.matchmaking.repository.standalone.FieldRepository;
import com.sqli.matchmaking.repository.standalone.SportRepository;

import lombok.Getter;

@Service
@Getter
public class FieldSportService {

    /* 
     * Repository
     */
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private SportRepository sportRepository;
    @Autowired
    private FieldSportRepository fsRepository;


    /* 
     * Booleans 
     */
    public boolean isSportInField(Field field, Sport sport) {
        Optional<FieldSport> find = fsRepository.findByFieldAndSport(field, sport);
        return find.isPresent();
    }
    

    /* 
     * Basic : Field
     */
    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    public Field getFieldById(Long id) {
        return fieldRepository.findById(id).orElse(null);
    }

    public void save(Field el) {
        fieldRepository.save(el);
    }

    /* 
     * Basic : Sport
     */
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    public Sport getSportById(Long id) {
        return sportRepository.findById(id).orElse(null);
    }

    public void save(Sport el) {
        sportRepository.save(el);
    }

    /* 
     * Basic : FieldSport
     */
    public void save(FieldSport el) {
        fsRepository.save(el);
    }

}
