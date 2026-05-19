package com.education.education.session.subSession.dto.response;

import java.util.List;

public record StackedAreaChart(
    List<StackedAreaChartData> completedSubSession,
    List<StackedAreaChartData> pendingSubSession,
    List<StackedAreaChartData> incompletedSubSession) {
}
