package com.sqli.matchmaking.data;

// utils
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
// spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
// entities
import com.sqli.matchmaking.model.associative.*;
import com.sqli.matchmaking.model.extension.*;
import com.sqli.matchmaking.model.standalone.*;
// services
import com.sqli.matchmaking.service.extension.*;
import com.sqli.matchmaking.service.extension.teammaking.forms.*;
import com.sqli.matchmaking.service.standalone.*;


import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final TeamService teamService;
    private final UserService userService;
    private final MatchService matchService;
    private final SportService sportService;
    private final FieldService fieldService;
    private final NotificationService notifService;

    @Autowired
    private RandomMaking random;
    @Autowired
    private EvenMaking even;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Delete teams
        teamService.getTeamUserRepository().deleteAll();
        teamService.getRepository().deleteAll();
        // Delete matches
        matchService.getMatchUserRepository().deleteAll();
        matchService.getRepository().deleteAll();
        // Delete fields and sports
        fieldService.getFsRepository().deleteAll();
        fieldService.getRepository().deleteAll();
        sportService.getRepository().deleteAll();
        // Delete notifications
        notifService.getRepository().deleteAll();
        // Delete users
        userService.getRepository().deleteAll();
        System.out.println("Data has been successfully deleted");

        insertFieldsAndSports();
        insertPlayers();
        insertMatches();
        playersJoinMatches();
        matchMaking();
        System.out.println("Data has been successfully inserted");
    }

    private final void insertFieldsAndSports() {
        // sport
        Sport football = Sport.builder().name("football").noTeams(2).build();
        Sport basketball = Sport.builder().name("basketball").noTeams(2).build();
        Sport volleyball = Sport.builder().name("volleyball").noTeams(2).build();

        sportService.saveAll(Arrays.asList(
                football, basketball, volleyball));

        // fields
        Field francoisBord = Field.builder().name("Francois Bord")
                .location("bordeaux")
                .noPlayers(22).build();
        Field doyenBruce = Field.builder().name("Doyen Bruce")
                .location("bordeaux")
                .noPlayers(22)
                .build();

        fieldService.saveAll(Arrays.asList(
                francoisBord, doyenBruce));

        // sports in fields
        fieldService.saveAllFieldSport(Arrays.asList(
                FieldSport.builder().field(francoisBord).sport(football).build(),
                FieldSport.builder().field(francoisBord).sport(volleyball).build(),
                FieldSport.builder().field(doyenBruce).sport(football).build(),
                FieldSport.builder().field(doyenBruce).sport(basketball).build()));
    }

    private final void insertPlayers() throws Exception {

        userService.saveAll(Arrays.asList(
                User.builder().firstName("Oussama")
                        .lastName("Zobid")
                        .email("oussama@example.com")
                        .password("passwordOussama")
                        .phone("0123456789")
                        .profileImage("/assets/Player6.svg")
                        .role("USER")
                        .rank(500)
                        .build(),
                User.builder().firstName("Mouad")
                        .lastName("Boumour")
                        .email("mouad@example.com")
                        .password("passwordMouad")
                        .phone("0123456789")
                        .profileImage("/assets/Player2.svg")
                        .role("ADMIN")
                        .rank(940)
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
                        .build()));
    }

    private final void insertMatches() {

        User zombid = userService.getByEmail("oussama@example.com");
        User mouad = userService.getByEmail("mouad@example.com");
        Field fb = fieldService.getRepository().findByName("Francois Bord").get();
        Field db = fieldService.getRepository().findByName("Doyen Bruce").get();
        Sport foot = sportService.getRepository().findByName("football").get();
        Sport basket = sportService.getRepository().findByName("basketball").get();

        matchService.saveAll(Arrays.asList(
                Match.builder().name("match dial lhobl")
                        .organizer(zombid)
                        .field(fb)
                        .sport(foot)
                        .date(Instant.now().plus(Duration.ofDays(2)))
                        .duration(Duration.ofMinutes(90))
                        .noPlayers(4)
                        .noSubs(2)
                        .description("haha")
                        .build(),
                Match.builder().name("match dial l7aya")
                        .organizer(mouad)
                        .field(db)
                        .sport(basket)
                        .date(Instant.now().plus(Duration.ofDays(8)))
                        .duration(Duration.ofMinutes(48))
                        .noPlayers(4)
                        .noSubs(2)
                        .description("haha")
                        .build()));
    }

    private final void playersJoinMatches() {
        // Players
        User mouad = userService.getByEmail("mouad@example.com");
        User achraf = userService.getByEmail("achraf@example.com");
        User oussama = userService.getByEmail("oussama@example.com");
        User hicham = userService.getByEmail("hicham@example.com");
        User salim = userService.getByEmail("salim@example.com");
        User anas = userService.getByEmail("anas@example.com");
        // Matches
        Match lhaya = matchService.getRepository().findMatchByName("match dial l7aya").get(0);
        Match lhobl = matchService.getRepository().findMatchByName("match dial lhobl").get(0);

        // Join
        matchService.saveAllJoins(Arrays.asList(
                // Match 1
                MatchUser.builder().match(lhaya).user(mouad).build(),
                MatchUser.builder().match(lhaya).user(anas).build(),
                MatchUser.builder().match(lhaya).user(salim).build(),
                MatchUser.builder().match(lhaya).user(oussama).build(),
                // Match 2
                MatchUser.builder().match(lhobl).user(achraf).build(),
                MatchUser.builder().match(lhobl).user(hicham).build(),
                MatchUser.builder().match(lhobl).user(oussama).build(),
                MatchUser.builder().match(lhobl).user(salim).build()));
    }

    private final void matchMaking() {
        // Matches
        Match lhaya = matchService.getRepository().findMatchByName("match dial l7aya").get(0);
        Match lhobl = matchService.getRepository().findMatchByName("match dial lhobl").get(0);

        // Close matches
        matchService.close(lhobl);
        matchService.close(lhaya);
        // Make matches
        matchService.makeTeams(lhobl, random);
        matchService.makeTeams(lhaya, even);
        // Form matches
        matchService.form(lhobl);
        matchService.form(lhaya);
    }
}

/*
 * // Get an image
 * String root = System.getProperty("user.dir");
 * Path defaultImagePath = Paths.get(root, "src", "main", "java", "com", "sqli",
 * "matchmaking");
 * String defaultImageFileName = "freddie.jpg";
 * String defaultImageUrl = Paths.get(defaultImagePath.toString(),
 * defaultImageFileName).toString();
 * ////byte[] defaultImageByte = User.getImageBytes(defaultImageUrl);
 */