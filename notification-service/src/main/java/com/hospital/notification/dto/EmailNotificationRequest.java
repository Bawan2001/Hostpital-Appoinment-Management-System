package com.hospital.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationRequest {

    @NotBlank(message = "Recipient ID is required")
    private String recipientId;

    @NotBlank(message = "Recipient Email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;

    @NotBlank(message = "Email subject is required")
    private String subject;

    @NotBlank(message = "Email message content is required")
    private String message;
}
