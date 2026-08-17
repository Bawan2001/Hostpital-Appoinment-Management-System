package com.hospital.patient.config;

import com.hospital.patient.entity.BloodGroup;
import com.hospital.patient.entity.Gender;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;

    @Override
    public void run(String... args) {
        if (patientRepository.count() == 0) {
            log.info("Seeding initial Patient Service data into MongoDB...");

            Patient p1 = Patient.builder()
                    .userId("65d0a12b-patient-001")
                    .name("Alice Johnson")
                    .email("alice.johnson@gmail.com")
                    .age(32)
                    .gender(Gender.FEMALE)
                    .bloodGroup(BloodGroup.O_POSITIVE)
                    .address("123 Galle Road, Colombo 03")
                    .medicalHistory(new ArrayList<>(List.of("Hypertension (diagnosed 2021)", "Penicillin Allergy")))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Patient p2 = Patient.builder()
                    .userId("65d0a12b-patient-002")
                    .name("Bob Smith")
                    .email("bob.smith@gmail.com")
                    .age(39)
                    .gender(Gender.MALE)
                    .bloodGroup(BloodGroup.A_POSITIVE)
                    .address("45 Kandy Road, Kiribathgoda")
                    .medicalHistory(new ArrayList<>(List.of("Type 2 Diabetes (diagnosed 2019)")))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Patient p3 = Patient.builder()
                    .userId("65d0a12b-patient-003")
                    .name("Kamal Perera")
                    .email("kamal.perera@gmail.com")
                    .age(24)
                    .gender(Gender.MALE)
                    .bloodGroup(BloodGroup.B_POSITIVE)
                    .address("78 Highlevel Road, Nugegoda")
                    .medicalHistory(new ArrayList<>(List.of("Asthma (mild)")))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            patientRepository.saveAll(List.of(p1, p2, p3));
            log.info("Initial Patient Service data seeded successfully. {} patients created.", 3);
        } else {
            log.info("Patient data already exists. Skipping seed.");
        }
    }
}
