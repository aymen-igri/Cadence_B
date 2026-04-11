package com.education.education.session.weeklySessionPlan.dto.request;

import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;

import java.time.LocalDateTime;

public record CreateWeeklySessionReq(
        LocalDateTime startTime,
        ESessionStatus status
) {
}
