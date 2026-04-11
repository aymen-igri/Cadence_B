package com.education.education.availability.dto.request;

import com.education.education.availability.availabilityPlan.dto.request.CreateAvailabilityPlanReq;
import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateAvailabilityRequest(
        @NotNull(message = "Availbility plan is required")
        CreateAvailabilityPlanReq availabilityPlan,

        @NotNull(message = "Availability slots are required")
        List<CreateAvailabilitySlotReq> slotsReq
){
}
