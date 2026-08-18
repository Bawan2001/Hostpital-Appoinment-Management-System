package com.hospital.auth.config;

import com.hospital.auth.entity.Role;
import com.hospital.auth.entity.User;
import com.hospital.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Seeding initial Auth Service users into MongoDB...");

            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@hospital.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .phone("+94770000000")
                    .build();

            User doctor = User.builder()
                    .name("Dr. Samantha Perera")
                    .email("doctor@hospital.com")
                    .password(passwordEncoder.encode("doctor123"))
                    .role(Role.DOCTOR)
                    .phone("+94771234567")
                    .build();

            User doctor2 = User.builder()
                    .name("Dr. Samantha Perera")
                    .email("samantha.perera@hospital.com")
                    .password(passwordEncoder.encode("doctor123"))
                    .role(Role.DOCTOR)
                    .phone("+94771234567")
                    .build();

            User patient = User.builder()
                    .name("Alice Johnson")
                    .email("patient@hospital.com")
                    .password(passwordEncoder.encode("patient123"))
                    .role(Role.PATIENT)
                    .phone("+94711112233")
                    .build();

            User patient2 = User.builder()
                    .name("Alice Johnson")
                    .email("alice.johnson@gmail.com")
                    .password(passwordEncoder.encode("patient123"))
                    .role(Role.PATIENT)
                    .phone("+94711112233")
                    .build();

            userRepository.saveAll(List.of(admin, doctor, doctor2, patient, patient2));
            log.info("Initial Auth Service data seeded successfully. 5 users created.");
        } else {
            log.info("Auth user data already exists. Skipping seed.");
        }
    }
}
