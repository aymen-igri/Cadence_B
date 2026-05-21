package com.education.education.session.weeklySessionPlan.mappers;

import com.education.education.session.weeklySessionPlan.dto.request.CreateWeeklySessionReq;
import com.education.education.session.weeklySessionPlan.dto.request.UpdateWeeklySessionReq;
import com.education.education.session.weeklySessionPlan.dto.response.ChartWeeklySessionPlanForUserRes;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
public class WeeklySessionPlanMapper {

  public WeeklySessionPlan toWeeklySessionPlan(CreateWeeklySessionReq request) {
    WeeklySessionPlan weeklySessionPlan = new WeeklySessionPlan();
    weeklySessionPlan.setTitle(request.title());
    weeklySessionPlan.setWeekYear(request.weekYear());
    weeklySessionPlan.setWeekNumber(request.weekNumber());
    weeklySessionPlan.setPlanStatus(EPlanStatus.PUBLISHED);

    return weeklySessionPlan;
  }

  public CreateWeeklySessionRes toCreateWeeklySessionRes(WeeklySessionPlan weeklySessionPlan) {
    return new CreateWeeklySessionRes(
        weeklySessionPlan.getId(),
        weeklySessionPlan.getWeekYear(),
        weeklySessionPlan.getWeekNumber(),
        weeklySessionPlan.getTitle(),
        weeklySessionPlan.getSessionStatus());
  }

  public void updateWeeklySessionFromReq(UpdateWeeklySessionReq request, WeeklySessionPlan weeklySessionPlan) {
    if (request.title() != null && !request.title().isBlank()) {
      weeklySessionPlan.setTitle(request.title());
    }

    if (request.startTime() != null) {
      weeklySessionPlan.setStartTime(request.startTime());
    }

    if (request.status() != null) {
      weeklySessionPlan.setSessionStatus(request.status());
    }
  }

  public ChartWeeklySessionPlanForUserRes toChartWeeklySessionPlanForUserRes(
      Integer totalWeeklySession,
      Integer completedWeeklySession,
      Integer pendingWeeklySession,
      Integer incompletedWeeklySession) {
    return new ChartWeeklySessionPlanForUserRes(
        totalWeeklySession,
        completedWeeklySession,
        pendingWeeklySession,
        incompletedWeeklySession);
  }
}
