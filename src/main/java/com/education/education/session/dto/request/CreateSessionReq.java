package com.education.education.session.dto.request;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.weeklySessionPlan.dto.request.CreateWeeklySessionReq;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateSessionReq(
        @NotNull(message = "Weekly session details are required")
        CreateWeeklySessionReq weeklySession,

        @NotNull(message = "Sub-sessions are required")
        List<CreateSubSessionReq> subSessions
) {
}
