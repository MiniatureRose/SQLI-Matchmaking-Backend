package com.sqli.matchmaking.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sqli.matchmaking.model.composite.*;
import com.sqli.matchmaking.model.standalone.*;
import com.sqli.matchmaking.request.DTOs;
import com.sqli.matchmaking.service.composite.*;
import com.sqli.matchmaking.service.matchmaking.*;
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

    @PutMapping("/{notificationId}")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(this.notificationService.markAsRead(notificationId));
    }

}
