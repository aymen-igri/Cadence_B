package com.education.education.goal.mappers;

import com.education.education.goal.dto.request.CreateGoalReq;
import com.education.education.goal.dto.request.UpdateGoalReq;
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
        goal.setSubject(subject);

        return goal;
    }

    public void updateGoalFromReq(UpdateGoalReq request, Goal goal) {
        if (request.title() != null && !request.title().isBlank()) {
            goal.setTitle(request.title());
        }
        if (request.targetHoursPerWeek() != null) {
            goal.setTargetHoursPerWeek(request.targetHoursPerWeek());
        }
        if (request.progress() != null) {
            goal.setProgress(request.progress());
        }
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
