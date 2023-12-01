package com.sqli.matchmaking;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sqli.matchmaking.model.*;
import com.sqli.matchmaking.model.composite.FieldSport;
import com.sqli.matchmaking.model.composite.Match;
import com.sqli.matchmaking.repository.*;
import com.sqli.matchmaking.repository.composite.FieldSportRepository;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;
import com.sqli.matchmaking.repository.composite.TeamRepository;
import com.sqli.matchmaking.repository.composite.MatchRepository;
import com.sqli.matchmaking.repository.composite.TeamUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final FieldRepository fieldRepository;
    private final SportRepository sportRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final TeamUserRepository teamUserRepository;
    private final MatchUserRepository matchUserRepository;
    private final FieldSportRepository fieldSportsRepository;

    @Override
    public void run(ApplicationArguments args) {
        
        teamUserRepository.deleteAll();
        matchUserRepository.deleteAll();
        teamRepository.deleteAll();
        matchRepository.deleteAll();
        fieldSportsRepository.deleteAll();
        fieldRepository.deleteAll();
        sportRepository.deleteAll();
        userRepository.deleteAll();
        
        System.out.println("Data has been successfully deleted");

        insertUsers();
        insertFieldsAndSports();
        insertMatchs();
        System.out.println("Data has been successfully inserted");
    }


    private final void insertFieldsAndSports() {
        // sport
        Sport football = Sport.builder().name("football").noTeams(2).build();
        Sport basketball = Sport.builder().name("basketball").noTeams(2).build();
        Sport volleyball = Sport.builder().name("volleyball").noTeams(2).build();

        // fields
        Field francoisBord = Field.builder().name("Francois Bord")
                                            .location("bordeaux")
                                            .noPlayers(22).
                                            build();
        Field doyenBruce = Field.builder().name("Doyen Bruce")
                                            .location("bordeaux")
                                            .noPlayers(22)
                                            .build();

        // sports in fields  
        fieldSportsRepository.saveAll(Arrays.asList(
            FieldSport.builder().field(francoisBord).sport(football).build(),
            FieldSport.builder().field(francoisBord).sport(volleyball).build(),
            FieldSport.builder().field(doyenBruce).sport(football).build(),
            FieldSport.builder().field(doyenBruce).sport(basketball).build()
        ));
    }


    private final void insertUsers() {

        userRepository.saveAll(Arrays.asList(
            User.builder().firstName("John")
                            .lastName("Decon")
                            .email("john@example.com")
                            .password("password123")
                            .phone("+337698895")
                            .role("USER")
                            .build(),
            User.builder().firstName("Alice")
                            .lastName("Decon")
                            .email("alice@example.com")
                            .password("password456")
                            .phone("+337698895")
                            .role("ADMIN")
                            .build(),
            User.builder().firstName("Bob")
                            .lastName("Decon")
                            .email("bob@example.com")
                            .password("password789")
                            .phone("+337698895")
                            .role("USER")
                            .build(),
            User.builder().firstName("Eve")
                            .lastName("Decon")
                            .email("eve@example.com")
                            .password("passwordabc")
                            .phone("+337698895")
                            .role("USER")
                            .build()
        ));
    }


    private final void insertMatchs() {

        User alice = userRepository.findByEmail("alice@example.com").get();
        User bob = userRepository.findByEmail("bob@example.com").get();
        Field fb = fieldRepository.findByName("Francois Bord").get();
        Field db = fieldRepository.findByName("Doyen Bruce").get();
        Sport foot = sportRepository.findByName("football").get();
        Sport basket = sportRepository.findByName("basketball").get();

        
        matchRepository.saveAll(Arrays.asList(
            Match.builder().name("match dial lhobl")
                .organizer(alice)
                .field(fb)
                .sport(foot)
                .date(Instant.now())
                .duration(Duration.ofMinutes(90))
                .noPlayers(10)
                .build(),
            Match.builder().name("match dial l7aya")
                .organizer(bob)
                .field(db)
                .sport(basket)
                .date(Instant.now())
                .duration(Duration.ofMinutes(48))
                .noPlayers(10)
                .build()
        ));
        
    }

}
