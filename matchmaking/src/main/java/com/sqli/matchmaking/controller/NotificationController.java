package com.sqli.matchmaking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.service.standalone.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(this.notificationService.getNotificationsByUser(userId));
    }

    @PutMapping("/MAR")
    public ResponseEntity<String> markAsRead(@RequestParam Long notificationId) {
        return ResponseEntity.ok(this.notificationService.markAsRead(notificationId));
    }

    @PostMapping("/deleteNotification")
    public ResponseEntity<String> deleteNotification(@RequestParam Long notificationId){
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("notification deleted successfully");
    }

    @PutMapping("/MAAR")
    public ResponseEntity<String> markAllAsRead(@RequestParam Long userId){
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked successfully ");
    }

}
