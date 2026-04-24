package com.education.education.goal.task.dto.request;

import com.education.education.goal.task.enums.ETask;

public record UpdateTaskReq (
        String title,
        String description,
        ETask status
){
}
