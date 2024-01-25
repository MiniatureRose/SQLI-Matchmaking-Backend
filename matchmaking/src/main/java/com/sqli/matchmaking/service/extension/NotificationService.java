package com.sqli.matchmaking.service.extension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import com.sqli.matchmaking.service.standalone.UserService;

import lombok.Getter;

import com.sqli.matchmaking.repository.associative.MatchUserRepository;
import com.sqli.matchmaking.repository.extension.MatchRepository;
import com.sqli.matchmaking.repository.extension.NotificationRepository;
import com.sqli.matchmaking.exception.Exceptions;
import com.sqli.matchmaking.exception.Exceptions.EntityIsNull;
import com.sqli.matchmaking.model.extension.*;
import com.sqli.matchmaking.model.standalone.*;

import java.util.Set;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.List;

@Service
@Getter
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchUserRepository matchUserRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private MatchService matchService;

    private Set<Match> Notifyedupcomingmatches = new HashSet<>();


    
    public List<Notification> getNotificationsByUser(User user) {
        return repository.findByUser(user);
    }

    @Scheduled(fixedRate = 3000) 
    public void sendUpcomingMatchNotifications() {
        Instant now = Instant.now();
        Instant in24Hours = now.plusSeconds(24 * 60 * 60);

        List<Match> upcomingMatches = matchRepository.findByDateBetween(now, in24Hours);

        for (Match match : upcomingMatches) {
            if(!Notifyedupcomingmatches.contains(match) && match.getStatus() != Match.CANCELED){
                List<User> usersToNotify = this.matchService.getMatchPlayers(match);
                for (User user : usersToNotify) {
                    String message = "Ton prochain match du " + formatDate(match.getDate()) + " commence dans moins de 24 heures.";
                    Notification el = this.create(user, message, Instant.now());
                    this.save(el);
                }
                Notifyedupcomingmatches.add(match);
            }
        }
    }

    public void sendCanceledMatchNotifications(Match match){
        List<User> usersToNotify = this.matchService.getMatchPlayers(match);
        for (User user : usersToNotify) {
            String message = "Le match du " + formatDate(match.getDate()) + " est annulé.";
            Notification el = this.create(user, message, Instant.now());
            this.save(el);
            }
        }

    public void sendTeamsCreatedNotifications(Match match){
        List<User> usersToNotify = this.matchService.getMatchPlayers(match);
            for (User user : usersToNotify) {
                String message = "Les equipes pour le match du " + formatDate(match.getDate()) + " sont crées.";
                Notification el = this.create(user, message, Instant.now());
                this.save(el);
            }
    }

    public void sendKickedOutNotifications(User user, Match match){
        String message = "Vous avez été retiré du match du " + formatDate(match.getDate());
        Notification el = this.create(user, message, Instant.now());
        this.save(el);
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 1 day
    public void sendSuggestionsNotifications() {
        List<User> allUsers = userService.getAll();
        allUsers.forEach(user -> {
            if (!isPlayingNextWeek(user)) {
                String message = user.getFirstName() + 
                ", ne manquez pas les matches de la semaine prochaine ! Inscrivez-vous dès maintenant";
                Notification el = this.create(user, message, Instant.now());
                this.save(el);
            }
        });
    }

    public Boolean isPlayingNextWeek(User user) {
        Instant currentInstant = Instant.now();
        Instant startOfNextWeek = currentInstant.plusSeconds(604800); 
        Instant endOfNextWeek = startOfNextWeek.plusSeconds(518400); 
        List<Match> userMatchesForNextWeek = matchUserRepository.findMatchOfUserForWeek(user, startOfNextWeek, endOfNextWeek);
        if (!userMatchesForNextWeek.isEmpty()) {
            return true; 
        } else {
            return false; 
        }
    }


    public void markAsRead(Notification el) {
        try {
            // Set
            el.setIsRead(true);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeUpdated(
                "Notification", "isRead -> true");
        }
        // Save
        this.save(el);
    }

    public void markAllAsRead(User user){
        List<Notification> notifications = this.getNotificationsByUser(user);
        for(Notification notification: notifications){
            markAsRead(notification);
        }
    }

    /*
     * Basic
     */
    public Notification getById(Long id) {
        if (id == null) {
            throw new EntityIsNull("Notification id");
        }
        return repository.findById(id)
            .orElseThrow(() -> 
                new Exceptions.EntityNotFound("Notification", "id", id)
            );
    }

    public void save(Notification el) {
        if (el == null) {
            throw new EntityIsNull("Notification");
        }
        try {
            // Save
            repository.save(el);
        } catch (DataIntegrityViolationException e) {
            throw new Exceptions.EntityCannotBeSaved("Notification");
        }
    }

    public void delete(Notification el){
        if (el == null) {
            throw new EntityIsNull("Notification");
        }
        try {
            // Delete
            repository.delete(el);
         } catch (DataIntegrityViolationException e) {
             throw new Exceptions.EntityCannotBeDeleted("Notification");
         }
    }

    public Notification create(User user, String message, Instant dateCreated ) {
        // Build
        Notification el = Notification.builder()
            .user(user)
            .dateCreated(dateCreated)
            .message(message)
            .build();
        return el;
    }

    private String formatDate(Instant instant) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE 'à' HH'h'", Locale.FRENCH);
        return localDateTime.format(formatter);
    }

}
