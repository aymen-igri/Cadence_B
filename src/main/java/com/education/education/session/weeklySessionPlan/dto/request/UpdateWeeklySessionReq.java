package com.education.education.session.weeklySessionPlan.dto.request;

import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateWeeklySessionReq(
        @NotNull(message = "Title is required")
        String title,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "Status is required")
        ESessionStatus status
) {
}
