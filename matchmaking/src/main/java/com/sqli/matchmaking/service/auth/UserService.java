package com.sqli.matchmaking.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.standalone.UserRepository;

import lombok.Getter;

import java.util.List;

@Getter
@Service
public class UserService {

    /* 
     * Repository
     */
    @Autowired
    private UserRepository repository;


    /* 
     * Booleans
     */
    public Boolean emailAlreadyExists(String email) {
        User find = repository.findByEmail(email).orElse(null);
        return find != null;
    }
    

    /* 
     * Basic
     */
    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> 
                new Exceptions.EntityNotFound("User", "id", id));
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> 
                new Exceptions.EntityNotFound("User", "email", email));
    }

    public void save(User el) {
        try {
            repository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("User");
        }
    }

    @Transactional
    public void updateRank(User user, Double newRank) {
        try {
            user.setRank(newRank);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated("User", "rank");
        }
        // Save it
        this.save(user);
    }

    public void onlyAdmin(User user) {
        if (!user.isAdmin())
            throw new Exceptions.OnlyAdmin();
    }

    public void onlyOrganizerAndAdmin(User user, Match match) {
        if (!match.getOrganizer().equals(user) && !user.isAdmin())
            throw new Exceptions.OnlyOrganizerAndAdmin();
    }


}
