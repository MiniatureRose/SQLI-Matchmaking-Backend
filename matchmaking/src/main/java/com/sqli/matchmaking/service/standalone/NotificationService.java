package com.sqli.matchmaking.service.standalone;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import com.sqli.matchmaking.repository.standalone.NotificationRepository;
import com.sqli.matchmaking.repository.standalone.UserRepository;
import com.sqli.matchmaking.repository.composite.MatchRepository;
import com.sqli.matchmaking.repository.composite.MatchUserRepository;
import com.sqli.matchmaking.service.composite.MatchService;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.model.composite.*;

import java.util.Set;
import java.util.HashSet;
import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MatchUserRepository matchUserRepository;

    @Autowired
    private MatchService matchService;

    private Set<Match> Notifyedupcomingmatches = new HashSet<>();
    private Set<Match> Notiyedteamscreatedmatches = new HashSet<>();
    
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Scheduled(fixedRate = 3000) 
    public void sendUpcomingMatchNotifications() {
        Instant now = Instant.now();
        System.out.println("test");
        Instant in24Hours = now.plusSeconds(24 * 60 * 60);

        List<Match> upcomingMatches = matchRepository.findByDateBetween(now, in24Hours);
        System.out.println(upcomingMatches);

        for (Match match : upcomingMatches) {
            if(!Notifyedupcomingmatches.contains(match)){
                List<User> usersToNotify = this.matchService.getMatchPlayers(match);

                for (User user : usersToNotify) {
                    String message = "Le match " + match.getId() + " commence dans moins de 24 heures.";
                    createNotification(user.getId(), message, Instant.now());
                    System.out.println(message);
                }
                Notifyedupcomingmatches.add(match);
            }
        }
    }

    public void SendCanceledMatchNotifications(Match match){
        List<User> usersToNotify = this.matchService.getMatchPlayers(match);
        for (User user : usersToNotify) {
            String message = "Le match " + match.getId() + " est annulé.";
            createNotification(user.getId(), message, Instant.now());
            System.out.println(message);
            }
        }

    public void SendTeamsCreatedNotifications(Match match){
        List<User> usersToNotify = this.matchService.getMatchPlayers(match);
            for (User user : usersToNotify) {
                String message = "Les equipes pour le match" + match.getId() + " sont crées.";
                createNotification(user.getId(), message, Instant.now());
                System.out.println(message);
            }
    }

    public void SendKickedOutNotifications(User user, Match match){
        String message = "Vous avez été retiré du match" + match.getId();
        createNotification(user.getId(), message, Instant.now());
        System.out.println(message);
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) //1 jour
    public void SendSuggestionsNotifications(){
        for(User user : userRepository.findAll()){
            if( !IsPlayingNextWeek(user.getId())){
                String message = user.getFirstName()+", ne manquez pas les matches de la semaine prochaine ! Inscrivez-vous dès maintenant";
                createNotification(user.getId(), message, Instant.now());
                System.out.println(message);
            }
        }
    }

    

    public boolean IsPlayingNextWeek(Long userId) {
    
        Instant currentInstant = Instant.now();
        Instant startOfNextWeek = currentInstant.plusSeconds(604800); 
        Instant endOfNextWeek = startOfNextWeek.plusSeconds(518400); 
        List<Match> userMatchesForNextWeek = matchUserRepository.findMatchOfUserForWeek(userId, startOfNextWeek, endOfNextWeek);
        if (!userMatchesForNextWeek.isEmpty()) {
            return true; 
        } else {
            return false; 
        }
    }


    public Notification createNotification(Long userId, String message, Instant dateCreated ) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setDateCreated(dateCreated);
        notification.setMessage(message);
        notification.setRead(false);


        return notificationRepository.save(notification);
    }

    public String markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
            return "MarkedAsRead";
        }
        return "notification not found";
    }

    public void markAllAsRead(Long userId){
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        for(Notification notification: notifications){
            markAsRead(notification.getId());
        }
    }
    
    public void deleteNotification(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notificationRepository.delete(notification);
        }
    }

}
