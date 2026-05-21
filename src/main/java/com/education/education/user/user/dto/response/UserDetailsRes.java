package com.education.education.user.user.dto.response;

import java.util.List;
import java.util.UUID;

import com.education.education.groups.DTO.response.ChartGroupsForUserRes;
import com.education.education.session.weeklySessionPlan.dto.response.ChartWeeklySessionPlanForUserRes;
import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;

public record UserDetailsRes(
    UUID id,
    String firstName,
    String lastName,
    EGender gender,
    String email,
    String phone,
    boolean isTotpEnabled,
    EStatus status,
    List<ChartGroupsForUserRes> groupsForUser,
    ChartWeeklySessionPlanForUserRes chartWeeklySessionPlanForUserRes) {
}
