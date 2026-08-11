package com.hospital.appointment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String appointmentDate;
    private String slotTime;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private String notes;
    private Double fee;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
