package com.education.education.session.subSession.services;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.dto.response.HeatMapChart;
import com.education.education.session.subSession.dto.response.HeatMapChartData;
import com.education.education.session.subSession.dto.response.HeatMapChartSubSessionData;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
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

  public HeatMapChart getHeatMapChartData(Integer weekNumber, Integer year) {

    if (weekNumber == null) {
      weekNumber = LocalDateTime.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    if (year == null) {
      year = LocalDateTime.now().get(IsoFields.WEEK_BASED_YEAR);
    }

    LocalDate startOfWeek = LocalDate.now()
        .withYear(year)
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
        .with(DayOfWeek.MONDAY);

    LocalDateTime startTimesTemp = startOfWeek.atStartOfDay();
    LocalDateTime endTimesTemp = startTimesTemp.plusDays(7);

    List<SubSession> subSessions = subSessionRepository.findAllByCreatedAtBetween(startTimesTemp, endTimesTemp);

    List<HeatMapChartData> daysList = new ArrayList<>();
    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      List<HeatMapChartSubSessionData> hoursList = new ArrayList<>();
      for (int h = 0; h < 24; h++) {
        hoursList.add(subSessionMapper.toHeatMapChartSubSessionData(
            LocalTime.of(h, 0),
            0));
      }
      daysList.add(subSessionMapper.toHeatMapChartData(dayOfWeek, hoursList));
    }

    for (SubSession ss : subSessions) {
      int dayidx = ss.getCreatedAt().getDayOfWeek().getValue() - 1;
      int hour = ss.getCreatedAt().getHour();

      HeatMapChartSubSessionData hourData = daysList.get(dayidx).subSessionData().get(hour);

      daysList.get(dayidx).subSessionData().set(
          hour,
          subSessionMapper.toHeatMapChartSubSessionData(
              hourData.creationHour(),
              hourData.subSessionCount().intValue() + 1));
    }

    return subSessionMapper.toHeatMapChart(startTimesTemp, daysList);
  }
}
