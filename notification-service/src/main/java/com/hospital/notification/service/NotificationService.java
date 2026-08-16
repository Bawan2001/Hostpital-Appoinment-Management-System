package com.hospital.notification.service;

import com.hospital.notification.dto.EmailNotificationRequest;
import com.hospital.notification.dto.NotificationResponse;
import com.hospital.notification.dto.SmsNotificationRequest;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendEmailNotification(EmailNotificationRequest request);
    NotificationResponse sendSmsNotification(SmsNotificationRequest request);
    NotificationResponse getNotificationById(String id);
    List<NotificationResponse> getNotificationsByUserId(String userId);
    List<NotificationResponse> getUnreadNotificationsByUserId(String userId);
    List<NotificationResponse> getAllNotifications();
    NotificationResponse markAsRead(String id);
    void deleteNotification(String id);
}
