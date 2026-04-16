package com.education.education.session.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GenerationSessionReq(
        List<UUID> goalsList,
        UUID availabilityPlanID,
        LocalDateTime weekStartDate
) {
}
