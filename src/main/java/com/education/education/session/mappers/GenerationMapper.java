package com.education.education.session.mappers;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilityPlan.repositories.AvailabilityPlanRepository;
import com.education.education.goal.entities.Goal;
import com.education.education.goal.repositories.GoalRepository;
import com.education.education.session.dto.middle.GenerationData;
import com.education.education.session.dto.request.GenerationSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.GenerationSessionRes;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@Transactional
@AllArgsConstructor
public class GenerationMapper {

    private final GoalRepository goalRepository;
    private final AvailabilityPlanRepository availabilityPlanRepository;
    
    public GenerationData toGenerationData(GenerationSessionReq req){
        List<Goal> goals = goalRepository.findAllById(req.goalsList());
        
        AvailabilityPlan availabilityPlan = availabilityPlanRepository.findById(req.availabilityPlanID()).
                orElseThrow(() -> new RuntimeException("Availability Plan not found with ID: " + req.availabilityPlanID()));

        return new GenerationData(
                goals,
                availabilityPlan,
                req.weekStartDate()
        );
    }

    public GenerationSessionRes toGeneratedSession(
            CreateSessionRes generatedSession,
            EPlanStatus planStatus,
            Long penaltyPoints
    ){
        return new GenerationSessionRes(
                generatedSession,
                planStatus,
                penaltyPoints
        );
    }

}
