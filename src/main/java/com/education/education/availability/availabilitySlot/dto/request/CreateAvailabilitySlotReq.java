package com.education.education.availability.availabilitySlot.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CreateAvailabilitySlotReq(
        @NotNull(message = "Day is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime start,

        @NotNull(message = "End time is required")
        LocalTime end
) {
}
