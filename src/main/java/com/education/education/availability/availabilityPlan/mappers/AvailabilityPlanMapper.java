package com.education.education.availability.availabilityPlan.mappers;

import com.education.education.availability.availabilityPlan.dto.request.CreateAvailabilityPlanReq;
import com.education.education.availability.availabilityPlan.dto.response.CreateAvailabilityPlanRes;
import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AvailabilityPlanMapper {

    public AvailabilityPlan toAvailabiliytPlan(CreateAvailabilityPlanReq request){
        AvailabilityPlan plan = new AvailabilityPlan();
        plan.setTitle(request.title());
        plan.setAvailabilityStatus(request.planStatus());

        return plan;
    }

    public CreateAvailabilityPlanRes toCreateAvailabilityPlanRes(AvailabilityPlan plan){
        return new CreateAvailabilityPlanRes(
                plan.getId(),
                plan.getTitle(),
                plan.getAvailabilityStatus(),
                plan.getCreatedAt()
        );
    }
}
