package com.hospital.notification.controller;

import com.hospital.notification.dto.EmailNotificationRequest;
import com.hospital.notification.dto.NotificationResponse;
import com.hospital.notification.dto.SmsNotificationRequest;
import com.hospital.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Notification Management", description = "Endpoints for sending email/SMS notifications and viewing alerts (Student 5)")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @Operation(summary = "Send an email alert to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Email notification dispatched",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email request payload")
    })
    public ResponseEntity<NotificationResponse> sendEmailNotification(@Valid @RequestBody EmailNotificationRequest request) {
        NotificationResponse response = notificationService.sendEmailNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sms")
    @Operation(summary = "Send an SMS alert to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SMS notification dispatched",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid SMS request payload")
    })
    public ResponseEntity<NotificationResponse> sendSmsNotification(@Valid @RequestBody SmsNotificationRequest request) {
        NotificationResponse response = notificationService.sendSmsNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable String id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notification history for a user")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable String userId) {
        List<NotificationResponse> response = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get only unread notifications for a user")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUserId(@PathVariable String userId) {
        List<NotificationResponse> response = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all notifications (Admin only)")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> response = notificationService.getAllNotifications();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable String id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification record")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
