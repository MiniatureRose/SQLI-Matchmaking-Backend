package com.sqli.matchmaking.service.standalone;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.exception.Exceptions.*;
import com.sqli.matchmaking.model.associative.FieldSport;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.associative.FieldSportRepository;
import com.sqli.matchmaking.repository.standalone.FieldRepository;

import lombok.Getter;

@Service
@Getter
public class FieldService {

    /* 
     * Repository
     */
    @Autowired
    private FieldRepository repository;
    @Autowired
    private FieldSportRepository fsRepository;

    

    /* 
     * Basic : Field
     */
    public List<Field> getAll() {
        return repository.findAll();
    }

    public Field getById(Long id) {
        if (id == null) {
            throw new EntityIsNull("Field");
        }
        return repository.findById(id)
            .orElseThrow(() -> 
                new EntityNotFound("Field", "id", id));
    }

    public void save(Field el) {
        if (el == null) {
            throw new EntityIsNull("Field");
        }
        try {
            repository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Field");
        }
    }

    public void saveAll(List<Field> list) {
        list.forEach(el -> this.save(el));
    }


    /* 
     * Basic : FieldSport
     */
    public boolean isSportInField(Field field, Sport sport) {
        Optional<FieldSport> find = fsRepository.findByFieldAndSport(field, sport);
        return find.isPresent();
    }

    public void save(FieldSport el) {
        if (el == null) {
            throw new EntityIsNull("FieldSport");
        }
        try {
            fsRepository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("FieldSport");
        }
    }

    public void saveAllFieldSport(List<FieldSport> list) {
        list.forEach(el -> this.save(el));
    }

}
