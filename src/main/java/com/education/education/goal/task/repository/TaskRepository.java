package com.education.education.goal.task.repository;

import com.education.education.goal.entities.Goal;
import com.education.education.goal.task.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByGoal(Goal goal);
}
