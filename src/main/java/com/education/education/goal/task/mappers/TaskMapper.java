package com.education.education.goal.task.mappers;

import com.education.education.goal.entities.Goal;
import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.dto.response.CreateTaskRes;
import com.education.education.goal.task.entities.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toTask(CreateTaskReq request, Goal goal) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setGoal(goal);
        return task;
    }

    public CreateTaskRes toCreateTaskRes(Task task) {
        return new CreateTaskRes(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }
}
