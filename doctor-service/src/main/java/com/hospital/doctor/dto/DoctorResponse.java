package com.hospital.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private String id;
    private String name;
    private String email;
    private String specialty;
    private String phone;
    private Integer experienceYears;
    private Double consultationFee;
    private String hospitalName;
    private List<String> availableDays;
    private Boolean isAvailable;
    private Instant createdAt;
}
