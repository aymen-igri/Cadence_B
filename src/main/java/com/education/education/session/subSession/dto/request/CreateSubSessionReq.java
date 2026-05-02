package com.education.education.session.subSession.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record CreateSubSessionReq(
        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "end time is required")
        LocalTime endTime,

        @NotNull(message = "Subject ID is required")
        UUID subjectId
) {
}
