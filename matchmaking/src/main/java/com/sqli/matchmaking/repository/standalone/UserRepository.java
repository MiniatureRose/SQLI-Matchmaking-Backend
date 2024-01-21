package com.sqli.matchmaking.repository.standalone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import com.sqli.matchmaking.model.standalone.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Override
    @NonNull
    Optional<User> findById(@NonNull Long id);

    Optional<User> findByEmail(String email);

    @Override
    void deleteById(@NonNull Long id);

    Boolean existsByEmail(String email);
}
