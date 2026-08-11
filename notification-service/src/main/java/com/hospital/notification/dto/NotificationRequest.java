package com.hospital.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Recipient ID is required")
    private String recipientId;

    private String recipientEmail;
    private String recipientPhone;

    @NotBlank(message = "Notification type (EMAIL/SMS) is required")
    private String type;

    private String subject;

    @NotBlank(message = "Notification message is required")
    private String message;
}
