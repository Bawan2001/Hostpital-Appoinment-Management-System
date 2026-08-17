package com.hospital.appointment.config;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.entity.AppointmentStatus;
import com.hospital.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        if (appointmentRepository.count() == 0) {
            log.info("Seeding initial Appointment Service data into MongoDB...");

            Appointment a1 = Appointment.builder()
                    .patientId("65d0a12b-patient-001")
                    .patientName("Alice Johnson")
                    .doctorId("doc-001")
                    .doctorName("Dr. Samantha Perera")
                    .appointmentDate(LocalDate.now().plusDays(3))
                    .slotTime("10:00 AM")
                    .status(AppointmentStatus.SCHEDULED)
                    .notes("Routine check-up for hypertension follow-up")
                    .fee(3500.0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Appointment a2 = Appointment.builder()
                    .patientId("65d0a12b-patient-002")
                    .patientName("Bob Smith")
                    .doctorId("doc-002")
                    .doctorName("Dr. Nimal Fernando")
                    .appointmentDate(LocalDate.now().plusDays(5))
                    .slotTime("02:00 PM")
                    .status(AppointmentStatus.SCHEDULED)
                    .notes("Diabetes Type 2 consultation and medication review")
                    .fee(4000.0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Appointment a3 = Appointment.builder()
                    .patientId("65d0a12b-patient-001")
                    .patientName("Alice Johnson")
                    .doctorId("doc-003")
                    .doctorName("Dr. Kavitha Rajapaksa")
                    .appointmentDate(LocalDate.now().minusDays(5))
                    .slotTime("11:30 AM")
                    .status(AppointmentStatus.COMPLETED)
                    .notes("Initial cardiology consultation")
                    .fee(2500.0)
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .updatedAt(LocalDateTime.now().minusDays(5))
                    .build();

            Appointment a4 = Appointment.builder()
                    .patientId("65d0a12b-patient-002")
                    .patientName("Bob Smith")
                    .doctorId("doc-001")
                    .doctorName("Dr. Samantha Perera")
                    .appointmentDate(LocalDate.now().minusDays(10))
                    .slotTime("09:00 AM")
                    .status(AppointmentStatus.CANCELLED)
                    .notes("Patient cancelled due to personal emergency")
                    .fee(3500.0)
                    .createdAt(LocalDateTime.now().minusDays(10))
                    .updatedAt(LocalDateTime.now().minusDays(10))
                    .build();

            appointmentRepository.saveAll(List.of(a1, a2, a3, a4));
            log.info("Initial Appointment Service data seeded successfully. 4 appointments created.");
        } else {
            log.info("Appointment data already exists. Skipping seed.");
        }
    }
}
