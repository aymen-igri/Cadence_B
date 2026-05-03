package com.education.education.session.weeklySessionPlan.repositories;

import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WeeklySessionPlanRepository extends JpaRepository<WeeklySessionPlan, UUID> {
    List<WeeklySessionPlan> findByUserOrderByStartTimeDesc(User user);

    List<WeeklySessionPlan> findAllByStartTimeBeforeAndSessionStatusNot(
            LocalDateTime endTime,
            ESessionStatus status);
}
