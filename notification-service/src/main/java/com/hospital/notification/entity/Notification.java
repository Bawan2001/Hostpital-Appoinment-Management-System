package com.hospital.notification.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String id;

    private String recipientId;

    private String recipientEmail;

    private String recipientPhone;

    private NotificationType type;

    private String subject;

    private String message;

    @Builder.Default
    private NotificationStatus status = NotificationStatus.SENT;

    @Builder.Default
    private Boolean isRead = false;

    @CreatedDate
    private LocalDateTime createdAt;
}
