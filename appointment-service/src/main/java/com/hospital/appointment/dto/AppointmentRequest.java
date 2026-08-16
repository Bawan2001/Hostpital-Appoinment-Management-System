package com.hospital.appointment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    private String patientName;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    private String doctorName;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate;

    @NotBlank(message = "Slot time is required (e.g. 10:30 AM)")
    private String slotTime;

    private String notes;

    @NotNull(message = "Consultation fee is required")
    @PositiveOrZero(message = "Fee cannot be negative")
    private Double fee;
}
