package com.sqli.matchmaking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sqli.matchmaking.Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    void deleteById(Integer id);
    Boolean existsByEmail(String email);
}