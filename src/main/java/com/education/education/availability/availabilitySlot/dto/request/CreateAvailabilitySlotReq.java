package com.education.education.availability.availabilitySlot.dto.request;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CreateAvailabilitySlotReq(
        DayOfWeek dayOfWeek,
        LocalTime start,
        LocalTime end
) {
}
