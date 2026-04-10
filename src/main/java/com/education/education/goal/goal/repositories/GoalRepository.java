package com.education.education.goal.goal.repositories;

import com.education.education.goal.goal.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
}
