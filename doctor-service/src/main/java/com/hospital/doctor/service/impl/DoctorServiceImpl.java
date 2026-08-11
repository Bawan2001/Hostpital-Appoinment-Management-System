package com.hospital.doctor.service.impl;

import com.hospital.doctor.dto.DoctorRequest;
import com.hospital.doctor.dto.DoctorResponse;
import com.hospital.doctor.entity.Doctor;
import com.hospital.doctor.repository.DoctorRepository;
import com.hospital.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .specialty(request.getSpecialty())
                .phone(request.getPhone())
                .experienceYears(request.getExperienceYears())
                .consultationFee(request.getConsultationFee())
                .hospitalName(request.getHospitalName())
                .availableDays(request.getAvailableDays())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return mapToDoctorResponse(savedDoctor);
    }

    @Override
    public DoctorResponse getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        return mapToDoctorResponse(doctor);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorResponse> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty).stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse updateAvailability(String id, Boolean isAvailable) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        doctor.setIsAvailable(isAvailable);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return mapToDoctorResponse(updatedDoctor);
    }

    private DoctorResponse mapToDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .email(doctor.getEmail())
                .specialty(doctor.getSpecialty())
                .phone(doctor.getPhone())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .hospitalName(doctor.getHospitalName())
                .availableDays(doctor.getAvailableDays())
                .isAvailable(doctor.getIsAvailable())
                .createdAt(doctor.getCreatedAt())
                .build();
    }
}
