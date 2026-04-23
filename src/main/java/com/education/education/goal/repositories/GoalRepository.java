package com.education.education.goal.repositories;

import com.education.education.goal.entities.Goal;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUser(User user);
    List<Goal> findByUserAndSubjectId(User user, UUID subjectId);
}
