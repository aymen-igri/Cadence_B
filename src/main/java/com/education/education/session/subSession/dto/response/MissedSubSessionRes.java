package com.education.education.session.subSession.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record MissedSubSessionRes(
        UUID id,
        UUID subjectId,
        String subjectName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime) {
}