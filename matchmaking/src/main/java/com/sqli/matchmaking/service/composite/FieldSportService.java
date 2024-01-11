package com.sqli.matchmaking.service.composite;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.composite.FieldSport;
import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;
import com.sqli.matchmaking.repository.composite.FieldSportRepository;

@Service
public class FieldSportService {

    @Autowired
    private FieldSportRepository fsRepository;

    public FieldSportRepository repository() {
        return fsRepository;
    }


    public List<FieldSport> getAll() {
        return fsRepository.findAll();
    }

    public FieldSport getById(Long id) {
        return fsRepository.findById(id).orElse(null);
    }

    public void save(FieldSport el) {
        fsRepository.save(el);
    }

    public void deleteById(Long id) {
        fsRepository.deleteById(id);
    }

    public boolean isSportInField(Field field, Sport sport) {
        Optional<FieldSport> find = fsRepository.findByFieldAndSport(field, sport);
        return find.isPresent();
    }

}
