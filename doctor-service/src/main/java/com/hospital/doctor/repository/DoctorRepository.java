package com.hospital.doctor.repository;

import com.hospital.doctor.entity.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findByIsAvailableTrue();
}
