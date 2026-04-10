package com.education.education.goal.dto.response;

import com.education.education.goal.goal.dto.response.CreateGoalRes;
import com.education.education.goal.task.dto.response.CreateTaskRes;

import java.util.List;

public record CreateGoalResponse(
        CreateGoalRes goal,
        List<CreateTaskRes> tasks
) {
}
