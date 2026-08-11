package com.hospital.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String id;
    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;
    private String type;
    private String subject;
    private String message;
    private String status;
    private Instant createdAt;
}
