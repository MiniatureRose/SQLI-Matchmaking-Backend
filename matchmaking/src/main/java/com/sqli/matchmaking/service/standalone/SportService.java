package com.sqli.matchmaking.service.standalone;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.standalone.SportRepository;

import java.util.List;

@Service
public class SportService {

    @Autowired
    private SportRepository sportRepository;

    public SportRepository repository() {
        return sportRepository;
    }
    

    public List<Sport> getAll() {
        return sportRepository.findAll();
    }

    public Sport getById(Long id) {
        return sportRepository.findById(id).orElse(null);
    }

    public void save(Sport el) {
        sportRepository.save(el);
    }

    public void deleteById(Long id) {
        sportRepository.deleteById(id);
    }

}