package com.education.education.session.weeklySessionPlan.mappers;

import com.education.education.session.weeklySessionPlan.dto.request.CreateWeeklySessionReq;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
public class WeeklySessionPlanMapper {

    public WeeklySessionPlan toWeeklySessionPlan(CreateWeeklySessionReq request) {
        WeeklySessionPlan weeklySessionPlan = new WeeklySessionPlan();
        weeklySessionPlan.setStartTime(request.startTime());
        weeklySessionPlan.setSessionStatus(request.status());

        return weeklySessionPlan;
    }

    public CreateWeeklySessionRes toCreateWeeklySessionRes(WeeklySessionPlan weeklySessionPlan) {
        return new CreateWeeklySessionRes(
                weeklySessionPlan.getId(),
                weeklySessionPlan.getStartTime(),
                weeklySessionPlan.getSessionStatus()
        );
    }
}
