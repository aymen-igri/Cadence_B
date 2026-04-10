package com.education.education.availability.dto.response;

import com.education.education.availability.availabilityPlan.dto.response.CreateAvailabilityPlanRes;
import com.education.education.availability.availabilitySlot.dto.response.CreateAvailabilitySlotRes;

import java.util.List;

public record CreateAvailabilityRes(
        CreateAvailabilityPlanRes plan,
        List<CreateAvailabilitySlotRes> slots
) {
}
