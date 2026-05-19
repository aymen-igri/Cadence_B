package com.education.education.session.subSession.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record StackedAreaChartData(
    DayOfWeek dayOfWeek,
    LocalTime startTime) {
}
