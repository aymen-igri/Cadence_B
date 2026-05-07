package com.education.education.session.subSession.dto.request;

import com.education.education.session.subSession.enums.ESubSessionStatus;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record UpdateSubSessionReq(
        @NotNull(message = "Id is required")
        UUID id,

        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @NotNull(message = "Status is required")
        ESubSessionStatus status,

        @NotNull(message = "Subject id is required")
        UUID subjectId
) {
}
