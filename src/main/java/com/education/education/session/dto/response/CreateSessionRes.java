package com.education.education.session.dto.response;

import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;

import java.util.List;

public record CreateSessionRes(
        CreateWeeklySessionRes weeklySession,
        List<CreateSubSessionRes> subSessions
) {
}
