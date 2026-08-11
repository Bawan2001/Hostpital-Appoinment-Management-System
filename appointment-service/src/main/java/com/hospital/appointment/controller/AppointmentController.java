package com.hospital.appointment.controller;

import com.hospital.appointment.dto.AppointmentRequest;
import com.hospital.appointment.dto.AppointmentResponse;
import com.hospital.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Endpoints for booking, scheduling, and cancelling hospital appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Book a new appointment")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "List appointments by Patient ID")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatientId(@PathVariable String patientId) {
        List<AppointmentResponse> list = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "List appointments by Doctor ID")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable String doctorId) {
        List<AppointmentResponse> list = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Operation(summary = "Get all appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable String id) {
        AppointmentResponse response = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status (SCHEDULED, COMPLETED, CANCELLED)")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable String id, @RequestParam String status) {
        AppointmentResponse response = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
