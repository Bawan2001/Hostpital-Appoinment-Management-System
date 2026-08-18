package com.hospital.auth.dto;

import com.hospital.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Builder.Default
    private String phone = "+94771234567";

    @NotNull(message = "Role is required (ADMIN, DOCTOR, or PATIENT)")
    private Role role;

    // Optional Patient fields
    private Integer age;
    private String gender;
    private String bloodGroup;
    private String address;

    // Optional Doctor fields
    private String specialty;
    private Integer experienceYears;
    private Double consultationFee;
    private String hospitalName;
    private java.util.List<String> availableDays;
}
