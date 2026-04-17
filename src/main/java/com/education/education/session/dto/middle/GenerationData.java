package com.education.education.session.dto.middle;

import com.education.education.goal.goal.entities.Goal;
import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;

import java.time.LocalDateTime;
import java.util.List;


public record GenerationData(
        List<Goal> goals,
        AvailabilityPlan availabilityPlan,
        LocalDateTime weekStartDate
) {
}
