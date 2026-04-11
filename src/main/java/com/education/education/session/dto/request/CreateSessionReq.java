package com.education.education.session.dto.request;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.weeklySessionPlan.dto.request.CreateWeeklySessionReq;

import java.util.List;

public record CreateSessionReq(
        CreateWeeklySessionReq weeklySession,
        List<CreateSubSessionReq> subSessions
) {
}
