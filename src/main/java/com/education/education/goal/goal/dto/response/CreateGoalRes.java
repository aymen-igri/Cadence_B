package com.education.education.goal.goal.dto.response;

import com.education.education.subject.entities.Subject;
import com.education.education.user.user.entities.User;

import java.util.UUID;

public record CreateGoalRes(
        UUID id,
        String title,
        float targetHoursPerWeek,
        float progress,
        UUID subjectId,
        String subjectName
) {
}
