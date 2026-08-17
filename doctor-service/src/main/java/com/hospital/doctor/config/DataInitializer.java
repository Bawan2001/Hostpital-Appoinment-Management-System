package com.hospital.doctor.config;

import com.hospital.doctor.entity.Doctor;
import com.hospital.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DoctorRepository doctorRepository;

    @Override
    public void run(String... args) {
        if (doctorRepository.count() == 0) {
            log.info("Seeding initial Doctor Service data into MongoDB...");

            Doctor d1 = Doctor.builder()
                    .name("Dr. Samantha Perera")
                    .email("samantha.perera@hospital.com")
                    .phone("+94771234567")
                    .specialty("Cardiology")
                    .experienceYears(15)
                    .consultationFee(3500.0)
                    .hospitalName("National Hospital Colombo")
                    .availableDays(List.of("Monday", "Wednesday", "Friday"))
                    .isAvailable(true)
                    .build();

            Doctor d2 = Doctor.builder()
                    .name("Dr. Nimal Fernando")
                    .email("nimal.fernando@hospital.com")
                    .phone("+94772345678")
                    .specialty("Neurology")
                    .experienceYears(12)
                    .consultationFee(4000.0)
                    .hospitalName("Asiri Central Hospital")
                    .availableDays(List.of("Tuesday", "Thursday", "Saturday"))
                    .isAvailable(true)
                    .build();

            Doctor d3 = Doctor.builder()
                    .name("Dr. Kavitha Rajapaksa")
                    .email("kavitha.rajapaksa@hospital.com")
                    .phone("+94773456789")
                    .specialty("Pediatrics")
                    .experienceYears(8)
                    .consultationFee(2500.0)
                    .hospitalName("Lady Ridgeway Hospital")
                    .availableDays(List.of("Monday", "Tuesday", "Thursday", "Friday"))
                    .isAvailable(true)
                    .build();

            Doctor d4 = Doctor.builder()
                    .name("Dr. Rohan Wickramasinghe")
                    .email("rohan.wickramasinghe@hospital.com")
                    .phone("+94774567890")
                    .specialty("Dermatology")
                    .experienceYears(10)
                    .consultationFee(3000.0)
                    .hospitalName("Durdans Hospital")
                    .availableDays(List.of("Wednesday", "Friday", "Sunday"))
                    .isAvailable(true)
                    .build();

            doctorRepository.saveAll(List.of(d1, d2, d3, d4));
            log.info("Initial Doctor Service data seeded successfully. {} doctors created.", 4);
        } else {
            log.info("Doctor data already exists. Skipping seed.");
        }
    }
}
