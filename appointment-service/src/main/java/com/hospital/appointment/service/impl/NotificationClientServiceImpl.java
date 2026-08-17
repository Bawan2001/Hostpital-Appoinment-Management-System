package com.hospital.appointment.service.impl;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.service.NotificationClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClientServiceImpl implements NotificationClientService {

    private final RestTemplate restTemplate;

    @Value("${app.notification-service-url:http://notification-service:8085}")
    private String notificationServiceUrl;

    @Value("${app.api-key:hospital-internal-secret-key-2026}")
    private String apiKey;

    @Override
    public void sendAppointmentConfirmationNotification(Appointment appointment) {
        String subject = "Appointment Confirmation - " + appointment.getAppointmentDate();
        String message = String.format(
                "Dear %s, your appointment with %s has been CONFIRMED for %s at %s. " +
                "Appointment ID: %s. Consultation fee: LKR %.2f. Please arrive 10 minutes early.",
                appointment.getPatientName(),
                appointment.getDoctorName(),
                appointment.getAppointmentDate(),
                appointment.getSlotTime(),
                appointment.getId(),
                appointment.getFee() != null ? appointment.getFee() : 0.0
        );

        String email = "patient-" + appointment.getPatientId() + "@hospital.com";
        sendEmail(appointment.getPatientId(), email, subject, message);
    }

    @Override
    public void sendAppointmentCancellationNotification(Appointment appointment) {
        String subject = "Appointment Cancellation - " + appointment.getAppointmentDate();
        String message = String.format(
                "Dear %s, your appointment with %s scheduled for %s at %s has been CANCELLED. " +
                "Appointment ID: %s. Please contact us to reschedule.",
                appointment.getPatientName(),
                appointment.getDoctorName(),
                appointment.getAppointmentDate(),
                appointment.getSlotTime(),
                appointment.getId()
        );

        String email = "patient-" + appointment.getPatientId() + "@hospital.com";
        sendEmail(appointment.getPatientId(), email, subject, message);
    }

    private void sendEmail(String recipientId, String email, String subject, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", apiKey);

            Map<String, String> body = new HashMap<>();
            body.put("recipientId", recipientId);
            body.put("recipientEmail", email);
            body.put("subject", subject);
            body.put("message", message);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            String endpoint = notificationServiceUrl + "/api/v1/notifications/email";
            restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
            log.info("Dispatched notification to {} via {}", recipientId, endpoint);
        } catch (Exception ex) {
            log.warn("Failed to dispatch notification to {}: {}", recipientId, ex.getMessage());
        }
    }
}
