package com.sqli.matchmaking.controller.extension;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.extension.Notification;
import com.sqli.matchmaking.service.extension.NotificationService;

@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("user")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(this.notificationService.getNotificationsByUser(userId));
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteNotification(@RequestParam @NonNull Long notificationId){
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("notification deleted successfully");
    }

    @PutMapping("MAR")
    public ResponseEntity<Object> markAsRead(@RequestParam @NonNull Long notificationId) {
        return ResponseEntity.ok(this.notificationService.markAsRead(notificationId));
    }

    @PutMapping("MAAR")
    public ResponseEntity<Object> markAllAsRead(@RequestParam Long userId){
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked successfully ");
    }

}
