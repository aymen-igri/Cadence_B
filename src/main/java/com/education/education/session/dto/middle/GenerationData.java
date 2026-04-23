package com.education.education.session.dto.middle;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.goal.entities.Goal;

import java.time.LocalDateTime;
import java.util.List;


public record GenerationData(
        List<Goal> goals,
        AvailabilityPlan availabilityPlan,
        LocalDateTime weekStartDate
) {
}
