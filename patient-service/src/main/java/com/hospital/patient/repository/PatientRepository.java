package com.hospital.patient.repository;

import com.hospital.patient.entity.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);
}
