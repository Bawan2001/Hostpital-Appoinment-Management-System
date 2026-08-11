package com.hospital.doctor.service;

import com.hospital.doctor.dto.DoctorRequest;
import com.hospital.doctor.dto.DoctorResponse;

import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest request);
    DoctorResponse getDoctorById(String id);
    List<DoctorResponse> getAllDoctors();
    List<DoctorResponse> getDoctorsBySpecialty(String specialty);
    DoctorResponse updateAvailability(String id, Boolean isAvailable);
}
