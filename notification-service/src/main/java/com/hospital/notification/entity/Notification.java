package com.hospital.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;
    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;
    private String type; // EMAIL, SMS
    private String subject;
    private String message;
    private String status; // SENT, READ

    @CreatedDate
    private Instant createdAt;
}
