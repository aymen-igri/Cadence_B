package com.education.education.session.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GenerationSessionReq(
        @NotNull(message = "Title is required")
        String title,

        @NotNull(message = "Goal list is required")
        List<UUID> goalsList,

        @NotNull(message = "Availability plan is required")
        UUID availabilityPlanID,

        @NotNull(message= "Start date is required")
        LocalDateTime weekStartDate,

        boolean usePriority
) {
}
