package com.education.education.availability.dto.request;

import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateAvailabilitySlotsRequest(
        @NotEmpty(message = "Availability slots are required")
        List<@Valid CreateAvailabilitySlotReq> slots
) {
}