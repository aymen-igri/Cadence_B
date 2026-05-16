package com.education.education.session.dto.response;

public record StruggleSubjectRes(
        String subjectId,
        String subjectName,
        int missedWeeksCount,
        String lastCompletedWeek) {
}