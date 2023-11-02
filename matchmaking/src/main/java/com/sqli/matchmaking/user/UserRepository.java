package com.sqli.matchmaking.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    void deleteById(Integer id);
    Boolean existsByEmail(String email);
}
