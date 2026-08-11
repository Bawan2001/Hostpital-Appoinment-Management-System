package com.hospital.notification.service;

import com.hospital.notification.dto.NotificationRequest;
import com.hospital.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendEmailNotification(NotificationRequest request);
    NotificationResponse sendSmsNotification(NotificationRequest request);
    List<NotificationResponse> getNotificationsByUserId(String userId);
    NotificationResponse markAsRead(String id);
}
