package com.sqli.matchmaking.service.standalone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.repository.standalone.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserRepository repository() {
        return userRepository;
    }
    

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void save(User el) {
        userRepository.save(el);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean emailAlreadyExists(String email) {
        User find = userRepository.findByEmail(email).orElse(null);
        return find != null;
    }

}
