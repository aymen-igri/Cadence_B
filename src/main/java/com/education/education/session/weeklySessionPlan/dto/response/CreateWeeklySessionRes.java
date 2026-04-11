package com.education.education.session.weeklySessionPlan.dto.response;

import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateWeeklySessionRes(
        UUID id,
        LocalDateTime startTime,
        ESessionStatus sessionStatus
) {
}
