package com.sqli.matchmaking.data;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.service.composite.*;
import com.sqli.matchmaking.service.standalone.*;
import com.sqli.matchmaking.service.matchmaking.RandomMaking;

////import java.nio.file.Path;
////import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final FieldService fieldService;
    private final SportService sportService;
    private final TeamService teamService;
    private final UserService userService;
    private final MatchService matchService;
    private final TeamUserService teamUserService;
    private final MatchUserService matchUserService;
    private final FieldSportService fieldSportsService;


    @Autowired
    private RandomMaking random;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        teamUserService.repository().deleteAll();
        matchUserService.repository().deleteAll();
        teamService.repository().deleteAll();
        matchService.repository().deleteAll();
        fieldSportsService.repository().deleteAll();
        fieldService.repository().deleteAll();
        sportService.repository().deleteAll();
        userService.repository().deleteAll();
        System.out.println("Data has been successfully deleted");

        insertFieldsAndSports();
        insertPlayers();
        insertMatchs();
        playersJoinMatches();
        matchMaking();
        System.out.println("Data has been successfully inserted");
    }

    
    private final void insertFieldsAndSports() {
        // sport
        Sport football = Sport.builder().name("football").noTeams(2).build();
        Sport basketball = Sport.builder().name("basketball").noTeams(2).build();
        Sport volleyball = Sport.builder().name("volleyball").noTeams(2).build();

        sportService.repository().saveAll(Arrays.asList(
            football, basketball, volleyball
        ));

        // fields
        Field francoisBord = Field.builder().name("Francois Bord")
                                            .location("bordeaux")
                                            .noPlayers(22).
                                            build();
        Field doyenBruce = Field.builder().name("Doyen Bruce")
                                            .location("bordeaux")
                                            .noPlayers(22)
                                            .build();

        fieldService.repository().saveAll(Arrays.asList(
            francoisBord, doyenBruce
        ));

        // sports in fields  
        fieldSportsService.repository().saveAll(Arrays.asList(
            FieldSport.builder().field(francoisBord).sport(football).build(),
            FieldSport.builder().field(francoisBord).sport(volleyball).build(),
            FieldSport.builder().field(doyenBruce).sport(football).build(),
            FieldSport.builder().field(doyenBruce).sport(basketball).build()
        ));
    }


    private final void insertPlayers() throws Exception {

        userService.repository().saveAll(Arrays.asList(
            User.builder().firstName("Oussama")
                            .lastName("Zobid")
                            .email("oussama@example.com")
                            .password("passwordOussama")
                            .phone("0123456789")
                            .profileImage("/assets/Player6.svg")
                            .role("USER")
                            .build(),
            User.builder().firstName("Mouad")
                            .lastName("Boumour")
                            .email("mouad@example.com")
                            .password("passwordMouad")
                            .phone("0123456789")
                            .profileImage("/assets/Player2.svg")
                            .role("ADMIN")
                            .build(),
            User.builder().firstName("Salim")
                            .lastName("Bekkari")
                            .email("salim@example.com")
                            .password("passwordSalim")
                            .phone("0123456789")
                            .profileImage("/assets/Player3.svg")
                            .role("USER")
                            .build(),
            User.builder().firstName("Achraf")
                            .lastName("Jdidi")
                            .email("achraf@example.com")
                            .password("passwordAchraf")
                            .phone("0123456789")
                            .profileImage("/assets/Player4.svg")
                            .role("USER")
                            .build(),
            User.builder().firstName("Hicham")
                            .lastName("Nekt")
                            .email("hicham@example.com")
                            .password("passwordHicham")
                            .phone("0123456789")
                            .profileImage("/assets/Player5.svg")
                            .role("USER")
                            .build(),
            User.builder().firstName("Anas")
                            .lastName("Naami")
                            .email("anas@example.com")
                            .password("passwordAnas")
                            .phone("0123456789")
                            .profileImage("/assets/Player7.svg")
                            .role("USER")
                            .build(),
            User.builder().firstName("Ayoub")
                            .lastName("Ziane")
                            .email("ayoub@example.com")
                            .password("passwordAyoub")
                            .phone("0123456789")
                            .profileImage("/assets/Player1.svg")
                            .role("USER")
                            .build()
        ));
    }


    private final void insertMatchs() {

        User zombid = userService.repository().findByEmail("oussama@example.com").get();
        User mouad = userService.repository().findByEmail("mouad@example.com").get();
        Field fb = fieldService.repository().findByName("Francois Bord").get();
        Field db = fieldService.repository().findByName("Doyen Bruce").get();
        Sport foot = sportService.repository().findByName("football").get();
        Sport basket = sportService.repository().findByName("basketball").get();

        matchService.repository().saveAll(Arrays.asList(
            Match.builder().name("match dial lhobl")
                .organizer(zombid)
                .field(fb)
                .sport(foot)
                .date(Instant.now().plus(Duration.ofDays(2)))
                .duration(Duration.ofMinutes(90))
                .noPlayers(4)
                .build(),
            Match.builder().name("match dial l7aya")
                .organizer(mouad)
                .field(db)
                .sport(basket)
                .date(Instant.now().plus(Duration.ofDays(8)))
                .duration(Duration.ofMinutes(48))
                .noPlayers(4)
                .build()
        ));
    }


    private final void playersJoinMatches() {
        // Players
        User mouad = userService.repository().findByEmail("mouad@example.com").get();
        User achraf = userService.repository().findByEmail("achraf@example.com").get();
        User oussama = userService.repository().findByEmail("oussama@example.com").get();
        User hicham = userService.repository().findByEmail("hicham@example.com").get();
        User salim = userService.repository().findByEmail("salim@example.com").get();
        User anas = userService.repository().findByEmail("anas@example.com").get();
        // Matches
        Match lhaya = matchService.repository().findMatchByName("match dial l7aya").get(0);
        Match lhobl = matchService.repository().findMatchByName("match dial lhobl").get(0);

        // Join
        matchUserService.saveAll(Arrays.asList(
            // Match 1
            MatchUser.builder().match(lhaya).user(mouad).build(),
            MatchUser.builder().match(lhaya).user(anas).build(),
            MatchUser.builder().match(lhaya).user(salim).build(),
            MatchUser.builder().match(lhaya).user(oussama).build(),
            // Match 2
            MatchUser.builder().match(lhobl).user(achraf).build(),
            MatchUser.builder().match(lhobl).user(hicham).build(),
            MatchUser.builder().match(lhobl).user(oussama).build(),
            MatchUser.builder().match(lhobl).user(salim).build()
        ));
    }

    private final void matchMaking() {
        // Matches
        Match lhaya = matchService.repository().findMatchByName("match dial l7aya").get(0);
        Match lhobl = matchService.repository().findMatchByName("match dial lhobl").get(0);

        random.createTeams(lhobl);
        random.makeJoin(lhobl);
        random.createTeams(lhaya);
        random.makeJoin(lhaya);
    }
}


    /* 
    // Get an image
    String root = System.getProperty("user.dir");
    Path defaultImagePath = Paths.get(root, "src", "main", "java", "com", "sqli", "matchmaking");
    String defaultImageFileName = "freddie.jpg";
    String defaultImageUrl = Paths.get(defaultImagePath.toString(), defaultImageFileName).toString();
    ////byte[] defaultImageByte = User.getImageBytes(defaultImageUrl);
    */