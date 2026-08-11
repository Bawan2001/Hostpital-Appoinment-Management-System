package com.hospital.patient.dto;

import com.hospital.patient.entity.BloodGroup;
import com.hospital.patient.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Patient name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address format")
    private String email;

    @NotNull(message = "Gender is required (MALE, FEMALE, OTHER)")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 130, message = "Age cannot exceed 130")
    private Integer age;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    private List<String> medicalHistory;
}
