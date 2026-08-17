package com.hospital.notification.config;

import com.hospital.notification.entity.Notification;
import com.hospital.notification.entity.NotificationStatus;
import com.hospital.notification.entity.NotificationType;
import com.hospital.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        if (notificationRepository.count() == 0) {
            log.info("Seeding initial Notification Service data into MongoDB...");

            Notification n1 = Notification.builder()
                    .recipientId("65d0a12b-patient-001")
                    .recipientEmail("alice.johnson@gmail.com")
                    .recipientPhone("+94711112233")
                    .type(NotificationType.EMAIL)
                    .subject("Welcome to MediCare Hospital Portal")
                    .message("Dear Alice, welcome to MediCare Hospital. Your patient profile has been activated successfully.")
                    .status(NotificationStatus.SENT)
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .build();

            Notification n2 = Notification.builder()
                    .recipientId("65d0a12b-patient-001")
                    .recipientEmail("alice.johnson@gmail.com")
                    .recipientPhone("+94711112233")
                    .type(NotificationType.SMS)
                    .subject("Appointment Confirmation")
                    .message("Your appointment with Dr. Samantha Perera is confirmed for 2026-08-20 at 10:00 AM.")
                    .status(NotificationStatus.SENT)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();

            Notification n3 = Notification.builder()
                    .recipientId("65d0a12b-patient-002")
                    .recipientEmail("bob.smith@gmail.com")
                    .recipientPhone("+94722223344")
                    .type(NotificationType.EMAIL)
                    .subject("Medical Record Updated")
                    .message("Dear Bob, your latest consultation notes have been added to your patient record.")
                    .status(NotificationStatus.SENT)
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .build();

            notificationRepository.saveAll(List.of(n1, n2, n3));
            log.info("Initial Notification Service data seeded successfully. 3 notifications created.");
        } else {
            log.info("Notification data already exists. Skipping seed.");
        }
    }
}
