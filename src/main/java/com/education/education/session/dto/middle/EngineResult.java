package com.education.education.session.dto.middle;

import com.education.education.session.subSession.entities.SubSession;

import java.util.List;

public record EngineResult(
        List<SubSession> subSessions,
        long penaltyPoints
) {
}
