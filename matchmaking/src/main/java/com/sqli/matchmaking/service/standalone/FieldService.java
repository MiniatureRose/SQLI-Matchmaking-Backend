package com.sqli.matchmaking.service.standalone;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.repository.standalone.FieldRepository;

import java.util.List;

@Service
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;
    

    public List<Field> getAll() {
        return fieldRepository.findAll();
    }

    public Field getById(Long id) {
        return fieldRepository.findById(id).orElse(null);
    }

    public void save(Field el) {
        fieldRepository.save(el);
    }

    public void deleteById(Long id) {
        fieldRepository.deleteById(id);
    }

}