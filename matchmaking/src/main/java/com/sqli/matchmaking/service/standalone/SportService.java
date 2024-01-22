package com.sqli.matchmaking.service.standalone;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.exception.Exceptions.*;
import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.standalone.SportRepository;

import lombok.Getter;

@Service
@Getter
public class SportService {

    /* 
     * Repository
     */
    @Autowired
    private SportRepository repository;


    /* 
     * Basic
     */
    public List<Sport> getAll() {
        return repository.findAll();
    }

    public Sport getById(Long id) {
        if (id == null) {
            throw new EntityIsNull("Sport");
        }
        return repository.findById(id)
            .orElseThrow(() -> 
            new EntityNotFound("Sport", "id", id));
    }

    public void save(Sport el) {
        if (el == null) {
            throw new EntityIsNull("Sport");
        }
        try {
            repository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Sport");
        }
    }

    public void saveAll(List<Sport> list) {
        list.forEach(el -> this.save(el));
    }

}
