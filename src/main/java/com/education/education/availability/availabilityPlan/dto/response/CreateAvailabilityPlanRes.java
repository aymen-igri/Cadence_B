package com.education.education.availability.availabilityPlan.dto.response;

import com.education.education.availability.availabilityPlan.enums.EAvailabilityStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAvailabilityPlanRes(
        UUID id,
        String title,
        EAvailabilityStatus availabilityStatus,
        LocalDateTime createdAt
) {
}
