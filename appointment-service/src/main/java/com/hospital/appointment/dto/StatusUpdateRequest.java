package com.hospital.appointment.dto;

import com.hospital.appointment.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {

    @NotNull(message = "Status is required (SCHEDULED, COMPLETED, CANCELLED)")
    private AppointmentStatus status;
}
