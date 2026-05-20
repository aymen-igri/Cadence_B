package com.education.education.session.subSession.dto.response;

import java.util.List;
import java.time.DayOfWeek;

public record HeatMapChartData(
    DayOfWeek dayOfWeek,
    List<HeatMapChartSubSessionData> subSessionData) {
}
