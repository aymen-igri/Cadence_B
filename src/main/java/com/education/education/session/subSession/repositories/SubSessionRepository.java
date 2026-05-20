package com.education.education.session.subSession.repositories;

import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SubSessionRepository extends JpaRepository<SubSession, UUID> {
  List<SubSession> findByWeeklySessionPlanOrderByStartTimeAsc(WeeklySessionPlan weeklySessionPlan);

  List<SubSession> findBySubSessionStatus(ESubSessionStatus status);

  List<SubSession> findAllByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
}
