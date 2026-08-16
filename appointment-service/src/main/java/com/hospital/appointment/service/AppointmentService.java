package com.hospital.appointment.service;

import com.hospital.appointment.dto.AppointmentRequest;
import com.hospital.appointment.dto.AppointmentResponse;
import com.hospital.appointment.dto.StatusUpdateRequest;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentById(String id);
    List<AppointmentResponse> getAllAppointments();
    List<AppointmentResponse> getAppointmentsByPatientId(String patientId);
    List<AppointmentResponse> getAppointmentsByDoctorId(String doctorId);
    List<AppointmentResponse> getAppointmentsByDate(LocalDate date);
    AppointmentResponse updateStatus(String id, StatusUpdateRequest request);
    AppointmentResponse cancelAppointment(String id);
    void deleteAppointment(String id);
}
