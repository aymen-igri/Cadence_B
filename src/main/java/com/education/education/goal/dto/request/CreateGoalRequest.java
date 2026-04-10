package com.education.education.goal.dto.request;

import com.education.education.goal.goal.dto.request.CreateGoalReq;
import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.entities.Task;

import java.util.List;

public record CreateGoalRequest(
        CreateGoalReq goal,
        List<CreateTaskReq> tasks
) {
}
