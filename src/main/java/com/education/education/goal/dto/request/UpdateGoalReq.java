package com.education.education.goal.dto.request;

public record UpdateGoalReq(
        String title,
        Float targetHoursPerWeek,
        Float progress,
        java.time.LocalDate deadline
) {
}
