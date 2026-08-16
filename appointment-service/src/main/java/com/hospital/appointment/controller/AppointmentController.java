package com.hospital.appointment.controller;

import com.hospital.appointment.dto.AppointmentRequest;
import com.hospital.appointment.dto.AppointmentResponse;
import com.hospital.appointment.dto.StatusUpdateRequest;
import com.hospital.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Endpoints for booking, searching, updating, and cancelling appointments (Student 4)")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Book a new appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment booked successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment retrieved",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> response = appointmentService.getAllAppointments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient ID")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatientId(@PathVariable String patientId) {
        List<AppointmentResponse> response = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor ID")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable String doctorId) {
        List<AppointmentResponse> response = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get appointments by specific date (YYYY-MM-DD)")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentResponse> response = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status (SCHEDULED, COMPLETED, CANCELLED)")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request) {
        AppointmentResponse response = appointmentService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an existing appointment")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable String id) {
        AppointmentResponse response = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete appointment record")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
