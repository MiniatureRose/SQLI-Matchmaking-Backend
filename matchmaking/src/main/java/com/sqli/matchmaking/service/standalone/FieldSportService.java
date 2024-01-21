package com.sqli.matchmaking.service.standalone;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.exception.Exceptions.*;
import com.sqli.matchmaking.model.associative.FieldSport;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.associative.FieldSportRepository;
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

    public Field getFieldById(@NonNull Long id) {
        return fieldRepository.findById(id)
            .orElseThrow(() -> 
                new EntityNotFound("Field", "id", id));
    }

    public void save(@NonNull Field el) {
        try {
            fieldRepository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Field");
        }
    }

    /* 
     * Basic : Sport
     */
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    public Sport getSportById(@NonNull Long id) {
        return sportRepository.findById(id)
            .orElseThrow(() -> 
            new EntityNotFound("Sport", "id", id));
    }

    public void save(@NonNull Sport el) {
        try {
            sportRepository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Sport");
        }
    }

    /* 
     * Basic : FieldSport
     */
    public void save(@NonNull FieldSport el) {
        try {
            fsRepository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("FieldSport");
        }
    }

}
