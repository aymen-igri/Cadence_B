package com.education.education.session.task.repository;

import com.education.education.session.task.entities.Task;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByWeeklySessionPlan(WeeklySessionPlan weeklySessionPlan);
}
