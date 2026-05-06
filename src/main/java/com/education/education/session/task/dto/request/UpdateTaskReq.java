package com.education.education.session.task.dto.request;

import com.education.education.session.task.enums.ETask;

public record UpdateTaskReq (
        String title,
        String description,
        ETask status
){
}
