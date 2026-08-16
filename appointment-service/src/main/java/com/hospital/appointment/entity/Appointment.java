package com.hospital.appointment.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    private String id;

    private String patientId;

    private String patientName;

    private String doctorId;

    private String doctorName;

    private LocalDate appointmentDate;

    private String slotTime;

    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    private String notes;

    private Double fee;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
