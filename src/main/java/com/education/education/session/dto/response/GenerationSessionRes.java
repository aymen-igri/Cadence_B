package com.education.education.session.dto.response;

import java.util.List;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;

public record GenerationSessionRes(
    CreateWeeklySessionRes weeklySession,
    List<CreateSubSessionRes> subSessions,
    EPlanStatus planStatus,
    Long penaltyPoints
) {
}
