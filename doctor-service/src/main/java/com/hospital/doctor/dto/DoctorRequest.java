package com.hospital.doctor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    private String phone;
    private Integer experienceYears;
    
    @NotNull(message = "Consultation fee is required")
    private Double consultationFee;

    private String hospitalName;
    private List<String> availableDays;
    private Boolean isAvailable;
}
