package com.education.education.session.subSession.dto.request;

import com.education.education.session.subSession.enums.ESubSessionStatus;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record UpdateSubSessionReq(
        UUID id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        ESubSessionStatus status,
        UUID subjectId
) {
}
