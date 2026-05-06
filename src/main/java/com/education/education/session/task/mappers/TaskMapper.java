package com.education.education.session.task.mappers;

import com.education.education.session.task.dto.request.CreateTaskReq;
import com.education.education.session.task.dto.request.UpdateTaskReq;
import com.education.education.session.task.dto.response.CreateTaskRes;
import com.education.education.session.task.entities.Task;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toTask(CreateTaskReq request, WeeklySessionPlan sessionPlan) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setWeeklySessionPlan(sessionPlan);
        return task;
    }

    public CreateTaskRes toCreateTaskRes(Task task) {
        return new CreateTaskRes(
                task.getId(),
                task.getWeeklySessionPlan().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }

    public void updateTaskFromReq(UpdateTaskReq request, Task task) {
        if (request.title() != null && !request.title().isBlank()) {
            task.setTitle(request.title());
        }
        if (request.description() != null && !request.description().isBlank()) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
    }
}
