package com.education.education.goal.mappers;

import com.education.education.goal.dto.request.CreateGoalReq;
import com.education.education.goal.dto.response.CreateGoalRes;
import com.education.education.goal.entities.Goal;
import com.education.education.subject.entities.Subject;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper {

    public Goal ToGoal(CreateGoalReq request, Subject subject){
        Goal goal = new Goal();
        goal.setTitle(request.title());
        goal.setTargetHoursPerWeek(request.targetHoursPerWeek());
        goal.setProgress(request.progress());
        goal.setDeadline(request.deadline());
        goal.setSubject(subject);

        return goal;
    }

    public CreateGoalRes toCreateGoalRes(Goal goal){
        return new CreateGoalRes(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetHoursPerWeek(),
                goal.getProgress(),
                goal.getDeadline(),
                goal.getSubject().getId(),
                goal.getSubject().getName()
        );
    }
}
