package com.education.education.session.weeklySessionPlan.dto.response;

public record ChartWeeklySessionPlanForUserRes(
    Integer totalWeeklySession,
    Integer activeWeeklySession,
    Integer completedWeeklySession,
    Integer incompletedWeeklySession,
    Integer pendingWeeklySession) {
}
