package com.education.education.session.subSession.dto.response;

import com.education.education.session.subSession.enums.ESubSessionStatus;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record CreateSubSessionRes(
        UUID id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        ESubSessionStatus status,
        UUID subjectId,
        String subjectName
) {
}
