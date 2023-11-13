package com.sqli.matchmaking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sqli.matchmaking.Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    void deleteById(Integer id);
    Boolean existsByEmail(String email);
<<<<<<< HEAD:matchmaking/src/main/java/com/sqli/matchmaking/user/UserRepository.java
}
=======
}
>>>>>>> 4ae5697545737fdd2afdc34ae164d9289b921967:matchmaking/src/main/java/com/sqli/matchmaking/Repository/UserRepository.java
