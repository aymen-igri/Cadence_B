package com.education.education.goal.dto.response;
import java.util.UUID;

public record CreateGoalRes(
        UUID id,
        String title,
        float targetHoursPerWeek,
        float progress,
        java.time.LocalDate deadline,
        UUID subjectId,
        String subjectName
) {
}
