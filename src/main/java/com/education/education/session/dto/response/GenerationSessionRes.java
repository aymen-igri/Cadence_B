package com.education.education.session.dto.response;

import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;

public record GenerationSessionRes(
    CreateSessionRes generatedSession,
    EPlanStatus planStatus,
    Long penaltyPoints
) {
}
