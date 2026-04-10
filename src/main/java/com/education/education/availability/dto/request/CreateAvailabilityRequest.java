package com.education.education.availability.dto.request;

import com.education.education.availability.availabilityPlan.dto.request.CreateAvailabilityPlanReq;
import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;

import java.util.List;

public record CreateAvailabilityRequest(
        CreateAvailabilityPlanReq availabilityPlan,
        List<CreateAvailabilitySlotReq> slotsReq
){
}
