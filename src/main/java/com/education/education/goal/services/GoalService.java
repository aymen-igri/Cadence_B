package com.education.education.goal.services;

import com.education.education.goal.dto.request.CreateGoalReq;
import com.education.education.goal.dto.response.CreateGoalRes;
import com.education.education.goal.entities.Goal;
import com.education.education.goal.mappers.GoalMapper;
import com.education.education.goal.repositories.GoalRepository;
import com.education.education.subject.entities.Subject;
import com.education.education.subject.repositories.SubjectRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class GoalService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public CreateGoalRes createGoal(
            UserDetails mainUser,
            CreateGoalReq goal,
            UUID subjectId
    ){
        if (goal.deadline() != null && goal.deadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
        
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        if (!subject.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to create a goal for this subject");
        }

        Goal newGoal= goalMapper.ToGoal(goal, subject);
        newGoal.setUser(user);
        Goal savedGoal = goalRepository.save(newGoal);

        return goalMapper.toCreateGoalRes(savedGoal);
    }

    public List<CreateGoalRes> getAllGoals(
        UserDetails userDetails,
        UUID subjectId
    ){
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        if (!subject.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to view goals for this subject");
        }

        List<Goal> goals = goalRepository.findByUserAndSubjectId(user, subjectId);

         return goals.stream()
                .map(goalMapper::toCreateGoalRes)
                .toList();
    }
}
