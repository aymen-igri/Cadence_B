package com.education.education.availability.availabilitySlot.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record CreateAvailabilitySlotRes(
        UUID id,
        DayOfWeek dayOfWeek,
        LocalTime start,
        LocalTime end,
        LocalDateTime createdAt
) {
}
