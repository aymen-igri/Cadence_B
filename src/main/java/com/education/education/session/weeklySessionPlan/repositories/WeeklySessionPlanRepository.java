package com.education.education.session.weeklySessionPlan.repositories;

import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeeklySessionPlanRepository extends JpaRepository<WeeklySessionPlan, UUID> {
}
