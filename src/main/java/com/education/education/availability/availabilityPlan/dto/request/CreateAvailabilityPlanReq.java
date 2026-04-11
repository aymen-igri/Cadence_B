package com.education.education.availability.availabilityPlan.dto.request;

import com.education.education.availability.availabilityPlan.enums.EAvailabilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAvailabilityPlanReq(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Plan status is required")
        EAvailabilityStatus planStatus
) {
}
