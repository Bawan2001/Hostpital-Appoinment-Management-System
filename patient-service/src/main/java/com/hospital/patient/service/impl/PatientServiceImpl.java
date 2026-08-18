package com.hospital.patient.service.impl;

import com.hospital.patient.dto.MedicalHistoryRequest;
import com.hospital.patient.dto.PatientRequest;
import com.hospital.patient.dto.PatientResponse;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.exception.PatientAlreadyExistsException;
import com.hospital.patient.exception.ResourceNotFoundException;
import com.hospital.patient.repository.PatientRepository;
import com.hospital.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new PatientAlreadyExistsException("Patient already registered with email: " + request.getEmail());
        }

        Patient patient = Patient.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .address(request.getAddress())
                .medicalHistory(request.getMedicalHistory() != null ? request.getMedicalHistory() : new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    @Override
    public PatientResponse getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse getPatientByUserId(String userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with userId: " + userId));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
        return mapToResponse(patient);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse updatePatient(String id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setName(request.getName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setAddress(request.getAddress());
        if (request.getMedicalHistory() != null) {
            patient.setMedicalHistory(request.getMedicalHistory());
        }
        patient.setUpdatedAt(LocalDateTime.now());

        Patient updated = patientRepository.save(patient);
        return mapToResponse(updated);
    }

    @Override
    public void deletePatient(String id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    public PatientResponse addMedicalHistory(String id, MedicalHistoryRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        if (patient.getMedicalHistory() == null) {
            patient.setMedicalHistory(new ArrayList<>());
        }
        patient.getMedicalHistory().add(request.getNote());
        patient.setUpdatedAt(LocalDateTime.now());
        Patient updated = patientRepository.save(patient);
        return mapToResponse(updated);
    }

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .userId(patient.getUserId())
                .name(patient.getName())
                .email(patient.getEmail())
                .age(patient.getAge())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .address(patient.getAddress())
                .medicalHistory(patient.getMedicalHistory())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
