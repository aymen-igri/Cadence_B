package com.education.education.session.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record GenerationSessionReq(
        @NotNull(message = "Title is required")
        String title,

        @NotNull(message = "Goal list is required")
        List<UUID> goalsList,

        @NotNull(message = "Availability plan is required")
        UUID availabilityPlanID,

        @NotNull(message= "Week year is required")
        Integer weekYear,

        @NotNull(message= "Week number is required")
        Integer weekNumber,

        boolean usePriority
) {
}
