package com.education.education.goal.goal.dto.request;

import java.util.UUID;

public record CreateGoalReq(
        UUID subjectId,
        String title,
        float targetHoursPerWeek,
        float progress
) {
}
