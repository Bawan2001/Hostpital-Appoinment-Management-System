package com.hospital.patient.dto;

import com.hospital.patient.entity.BloodGroup;
import com.hospital.patient.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private String id;
    private String userId;
    private String name;
    private String email;
    private Integer age;
    private Gender gender;
    private BloodGroup bloodGroup;
    private String address;
    private List<String> medicalHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
