package com.hospital.appointment.service;

import com.hospital.appointment.entity.Appointment;

public interface NotificationClientService {
    void sendAppointmentConfirmationNotification(Appointment appointment);
    void sendAppointmentCancellationNotification(Appointment appointment);
}
