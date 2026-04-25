package com.education.education.session.dto.request;

import com.education.education.session.subSession.dto.request.UpdateSubSessionReq;
import com.education.education.session.weeklySessionPlan.dto.request.UpdateWeeklySessionReq;
import jakarta.validation.Valid;

import java.util.List;

public record UpdateSessionReq(
        @Valid
        UpdateWeeklySessionReq weeklySession,

        @Valid
        List<UpdateSubSessionReq> subSessions
) {
}
