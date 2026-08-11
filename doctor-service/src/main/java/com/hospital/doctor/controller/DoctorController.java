package com.hospital.doctor.controller;

import com.hospital.doctor.dto.DoctorRequest;
import com.hospital.doctor.dto.DoctorResponse;
import com.hospital.doctor.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Endpoints for managing doctor profiles, specialties, and schedule availability")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @Operation(summary = "Create doctor profile")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor profile by ID")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable String id) {
        DoctorResponse response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        List<DoctorResponse> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialty/{specialty}")
    @Operation(summary = "Search doctors by specialty")
    public ResponseEntity<List<DoctorResponse>> getDoctorsBySpecialty(@PathVariable String specialty) {
        List<DoctorResponse> doctors = doctorService.getDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}/availability")
    @Operation(summary = "Update doctor availability status")
    public ResponseEntity<DoctorResponse> updateAvailability(@PathVariable String id, @RequestParam Boolean isAvailable) {
        DoctorResponse response = doctorService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(response);
    }
}
