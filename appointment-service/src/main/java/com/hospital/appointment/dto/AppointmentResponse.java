package com.hospital.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String appointmentDate;
    private String slotTime;
    private String status;
    private String notes;
    private Double fee;
    private Instant createdAt;
}
