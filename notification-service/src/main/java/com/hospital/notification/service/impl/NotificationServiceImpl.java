package com.hospital.notification.service.impl;

import com.hospital.notification.dto.EmailNotificationRequest;
import com.hospital.notification.dto.NotificationResponse;
import com.hospital.notification.dto.SmsNotificationRequest;
import com.hospital.notification.entity.Notification;
import com.hospital.notification.entity.NotificationStatus;
import com.hospital.notification.entity.NotificationType;
import com.hospital.notification.exception.ResourceNotFoundException;
import com.hospital.notification.repository.NotificationRepository;
import com.hospital.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse sendEmailNotification(EmailNotificationRequest request) {
        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .recipientEmail(request.getRecipientEmail())
                .type(NotificationType.EMAIL)
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(NotificationStatus.SENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    public NotificationResponse sendSmsNotification(SmsNotificationRequest request) {
        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .recipientPhone(request.getRecipientPhone())
                .type(NotificationType.SMS)
                .message(request.getMessage())
                .status(NotificationStatus.SENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    public NotificationResponse getNotificationById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapToResponse(notification);
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        return notificationRepository.findByRecipientId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadNotificationsByUserId(String userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    @Override
    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .recipientEmail(notification.getRecipientEmail())
                .recipientPhone(notification.getRecipientPhone())
                .type(notification.getType())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
