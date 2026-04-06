package com.education.education.notification.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.education.education.notification.DTO.NotificationDTO;
import com.education.education.notification.services.NotificationService;
import com.education.education.user.user.wrapper.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/getAll")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
        throw new RuntimeException("userDetails is null");
       }
        String userId = userDetails.user.getId().toString();
        return ResponseEntity.ok(
            notificationService.getUserNotifications(userId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userId = userDetails.user.getId().toString();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userId = userDetails.user.getId().toString();
        return ResponseEntity.ok(
            notificationService.getUnreadCount(userId)
        );
    }
}