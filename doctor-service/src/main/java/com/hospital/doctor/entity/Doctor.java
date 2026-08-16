package com.hospital.doctor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {

    @Id
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

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
