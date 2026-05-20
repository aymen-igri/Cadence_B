package com.education.education.session.subSession.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record HeatMapChart(
    LocalDateTime localDateTime,
    List<HeatMapChartData> heatMapChartData) {
}
