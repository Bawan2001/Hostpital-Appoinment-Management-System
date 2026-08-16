package com.hospital.notification.dto;

import com.hospital.notification.entity.NotificationStatus;
import com.hospital.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private String id;
    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationStatus status;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
