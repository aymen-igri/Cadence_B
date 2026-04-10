package com.education.education.availability.availabilityPlan.dto.request;

import com.education.education.availability.availabilityPlan.enums.EAvailabilityStatus;

public record CreateAvailabilityPlanReq(
        String title,
        EAvailabilityStatus planStatus
) {
}
