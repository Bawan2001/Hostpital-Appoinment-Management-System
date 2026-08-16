package com.hospital.appointment.dto;

import com.hospital.appointment.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDate appointmentDate;
    private String slotTime;
    private AppointmentStatus status;
    private String notes;
    private Double fee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
