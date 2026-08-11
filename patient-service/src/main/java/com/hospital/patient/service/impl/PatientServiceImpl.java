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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByUserId(request.getUserId())) {
            throw new PatientAlreadyExistsException("Patient profile already exists for userId: " + request.getUserId());
        }

        Patient patient = Patient.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .email(request.getEmail())
                .gender(request.getGender())
                .age(request.getAge())
                .address(request.getAddress())
                .bloodGroup(request.getBloodGroup())
                .medicalHistory(request.getMedicalHistory() != null ? request.getMedicalHistory() : new ArrayList<>())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return mapToPatientResponse(savedPatient);
    }

    @Override
    public PatientResponse updatePatient(String id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setGender(request.getGender());
        patient.setAge(request.getAge());
        patient.setAddress(request.getAddress());
        patient.setBloodGroup(request.getBloodGroup());
        
        if (request.getMedicalHistory() != null) {
            patient.setMedicalHistory(request.getMedicalHistory());
        }

        Patient updatedPatient = patientRepository.save(patient);
        return mapToPatientResponse(updatedPatient);
    }

    @Override
    public PatientResponse getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return mapToPatientResponse(patient);
    }

    @Override
    public PatientResponse getPatientByUserId(String userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for userId: " + userId));
        return mapToPatientResponse(patient);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse addMedicalHistory(String id, MedicalHistoryRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        if (patient.getMedicalHistory() == null) {
            patient.setMedicalHistory(new ArrayList<>());
        }
        patient.getMedicalHistory().add(request.getNote());

        Patient updatedPatient = patientRepository.save(patient);
        return mapToPatientResponse(updatedPatient);
    }

    @Override
    public void deletePatient(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .userId(patient.getUserId())
                .name(patient.getName())
                .email(patient.getEmail())
                .gender(patient.getGender())
                .age(patient.getAge())
                .address(patient.getAddress())
                .bloodGroup(patient.getBloodGroup())
                .medicalHistory(patient.getMedicalHistory())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
