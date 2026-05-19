package com.education.education.session.subSession.services;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.dto.response.StackedAreaChart;
import com.education.education.session.subSession.dto.response.StackedAreaChartData;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.subSession.mappers.SubSessionMapper;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class SubSessionService {

  private final SubSessionRepository subSessionRepository;
  private final SubSessionMapper subSessionMapper;

  public CreateSubSessionRes createSubSession(
      CreateSubSessionReq request,
      WeeklySessionPlan weeklySessionPlan) {
    SubSession newSubSession = subSessionMapper.toSubSession(request);
    newSubSession.setWeeklySessionPlan(weeklySessionPlan);

    return subSessionMapper.toCreateSubSessionRes(subSessionRepository.save(newSubSession));
  }

  public List<CreateSubSessionRes> getSubSessionsByPlan(WeeklySessionPlan weeklySessionPlan) {
    return subSessionRepository.findByWeeklySessionPlanOrderByStartTimeAsc(weeklySessionPlan).stream()
        .map(subSessionMapper::toCreateSubSessionRes)
        .toList();
  }

  public StackedAreaChart getStackedAreaChartData() {
    List<SubSession> completedSubSession = subSessionRepository.findBySubSessionStatus(ESubSessionStatus.COMPLETED);
    List<SubSession> pendingSubSession = subSessionRepository.findBySubSessionStatus(ESubSessionStatus.PENDING);
    List<SubSession> incompletedSubSession = subSessionRepository.findBySubSessionStatus(ESubSessionStatus.INCOMPLETED);

    List<StackedAreaChartData> completedSubSessionData = new ArrayList<>();
    List<StackedAreaChartData> pendingSubSessionData = new ArrayList<>();
    List<StackedAreaChartData> incompletedSubSessionData = new ArrayList<>();

    completedSubSession
        .forEach((subSession) -> completedSubSessionData.add(subSessionMapper.toStackedAreaChartData(subSession)));
    pendingSubSession
        .forEach((subSession) -> pendingSubSessionData.add(subSessionMapper.toStackedAreaChartData(subSession)));
    incompletedSubSession
        .forEach((subSession) -> incompletedSubSessionData.add(subSessionMapper.toStackedAreaChartData(subSession)));

    return subSessionMapper.toStackedAreaChart(
        completedSubSessionData,
        pendingSubSessionData,
        incompletedSubSessionData);
  }
}
