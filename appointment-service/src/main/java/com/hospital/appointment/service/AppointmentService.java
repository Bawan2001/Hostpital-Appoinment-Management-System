package com.hospital.appointment.service;

import com.hospital.appointment.dto.AppointmentRequest;
import com.hospital.appointment.dto.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentById(String id);
    List<AppointmentResponse> getAppointmentsByPatientId(String patientId);
    List<AppointmentResponse> getAppointmentsByDoctorId(String doctorId);
    List<AppointmentResponse> getAllAppointments();
    AppointmentResponse updateStatus(String id, String status);
    AppointmentResponse cancelAppointment(String id);
}
