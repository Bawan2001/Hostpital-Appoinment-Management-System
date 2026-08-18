package com.hospital.patient.service;

import com.hospital.patient.dto.MedicalHistoryRequest;
import com.hospital.patient.dto.PatientRequest;
import com.hospital.patient.dto.PatientResponse;

import java.util.List;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request);
    PatientResponse getPatientById(String id);
    PatientResponse getPatientByUserId(String userId);
    PatientResponse getPatientByEmail(String email);
    List<PatientResponse> getAllPatients();
    PatientResponse updatePatient(String id, PatientRequest request);
    void deletePatient(String id);
    PatientResponse addMedicalHistory(String id, MedicalHistoryRequest request);
}
