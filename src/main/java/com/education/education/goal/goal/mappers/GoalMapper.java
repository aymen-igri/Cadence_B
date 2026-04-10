package com.education.education.goal.goal.mappers;

import com.education.education.goal.goal.dto.request.CreateGoalReq;
import com.education.education.goal.goal.dto.response.CreateGoalRes;
import com.education.education.goal.goal.entities.Goal;
import com.education.education.subject.repositories.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
public class GoalMapper {

    private final SubjectRepository subjectRepository;

    public Goal ToGoal(CreateGoalReq request){
        Goal goal = new Goal();
        goal.setTitle(request.title());
        goal.setTargetHoursPerWeek(request.targetHoursPerWeek());
        goal.setProgress(request.progress());
        goal.setSubject(subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));

        return goal;
    }

    public CreateGoalRes toCreateGoalRes(Goal goal){
        return new CreateGoalRes(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetHoursPerWeek(),
                goal.getProgress(),
                goal.getSubject().getId(),
                goal.getSubject().getName()
        );
    }
}
