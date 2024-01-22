package com.sqli.matchmaking.controller.extension;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.extension.Notification;
import com.sqli.matchmaking.model.standalone.User;
import com.sqli.matchmaking.service.extension.NotificationService;
import com.sqli.matchmaking.service.standalone.UserService;

@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("user")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@RequestParam Long userId) {
        User user = userService.getById(userId);
        return ResponseEntity.ok(this.notificationService.getNotificationsByUser(user));
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteNotification(@RequestParam @NonNull Long notificationId) {
        Notification notif = notificationService.getById(notificationId);
        notificationService.delete(notif);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    @PutMapping("MAR")
    public ResponseEntity<Object> markAsRead(@RequestParam @NonNull Long notificationId) {
        Notification notif = notificationService.getById(notificationId);
        this.notificationService.markAsRead(notif);
        return ResponseEntity.ok("Notification marked successfully");
    }

    @PutMapping("MAAR")
    public ResponseEntity<Object> markAllAsRead(@RequestParam Long userId) {
        User user = userService.getById(userId);
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok("All notifications marked successfully");
    }

}
