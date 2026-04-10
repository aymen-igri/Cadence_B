package com.education.education.goal.task.mappers;

import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.dto.response.CreateTaskRes;
import com.education.education.goal.task.entities.Task;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
public class TaskMapper {

    public Task toTask(CreateTaskReq request){
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());

        return task;
    }

    public CreateTaskRes toCreateTaskRes(Task task){
        return new CreateTaskRes(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }
}
