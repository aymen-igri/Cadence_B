package com.education.education.session.weeklySessionPlan.dto.response;

import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;

import java.util.UUID;

public record CreateWeeklySessionRes(
                UUID id,
                Integer weekYear,
                Integer weekNumber,
                String title,
                ESessionStatus sessionStatus) {
}
