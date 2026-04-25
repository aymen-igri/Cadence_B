package com.education.education.session.subSession.repositories;

import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubSessionRepository extends JpaRepository<SubSession, UUID> {
    List<SubSession> findByWeeklySessionPlanOrderByStartTimeAsc(WeeklySessionPlan weeklySessionPlan);
}
