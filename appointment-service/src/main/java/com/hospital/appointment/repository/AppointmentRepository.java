package com.hospital.appointment.repository;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.entity.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByDoctorId(String doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
    List<Appointment> findByDoctorIdAndAppointmentDate(String doctorId, LocalDate appointmentDate);
}
