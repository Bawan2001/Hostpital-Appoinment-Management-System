package com.hospital.patient.dto;

import com.hospital.patient.entity.BloodGroup;
import com.hospital.patient.entity.Gender;
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
public class PatientResponse {
    private String id; // patientId
    private String userId;
    private String name;
    private String email;
    private Gender gender;
    private Integer age;
    private String address;
    private BloodGroup bloodGroup;
    private List<String> medicalHistory;
    private Instant createdAt;
}
