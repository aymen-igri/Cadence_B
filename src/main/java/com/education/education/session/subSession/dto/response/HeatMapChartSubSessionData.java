package com.education.education.session.subSession.dto.response;

import java.time.LocalTime;

public record HeatMapChartSubSessionData(
    LocalTime creationHour,
    Number subSessionCount) {
}
