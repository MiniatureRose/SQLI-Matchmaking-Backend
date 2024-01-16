package com.sqli.matchmaking.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.standalone.UserRepository;

import lombok.Getter;

import java.util.List;

@Service
@Getter
public class UserService {

    /* 
     * Repository
     */
    @Autowired
    private UserRepository repository;


    /* 
     * Booleans
     */
    public boolean emailAlreadyExists(String email) {
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
        return repository.findById(id).orElse(null);
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public void save(User el) {
        repository.save(el);
    }

}
