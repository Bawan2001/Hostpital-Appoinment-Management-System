package com.hospital.notification.controller;

import com.hospital.notification.dto.NotificationRequest;
import com.hospital.notification.dto.NotificationResponse;
import com.hospital.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Endpoints for sending email/SMS notifications and tracking alert history")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @Operation(summary = "Send email notification alert")
    public ResponseEntity<NotificationResponse> sendEmailNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendEmailNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sms")
    @Operation(summary = "Send SMS alert")
    public ResponseEntity<NotificationResponse> sendSmsNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendSmsNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notification history by User ID")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable String userId) {
        List<NotificationResponse> list = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as READ")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable String id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }
}
