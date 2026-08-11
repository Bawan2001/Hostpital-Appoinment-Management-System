package com.hospital.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    private String patientName;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    private String doctorName;

    @NotBlank(message = "Appointment date is required")
    private String appointmentDate;

    @NotBlank(message = "Slot time is required")
    private String slotTime;

    private String notes;
    private Double fee;
}
