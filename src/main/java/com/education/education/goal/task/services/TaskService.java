package com.education.education.goal.task.services;

import com.education.education.goal.goal.entities.Goal;
import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.dto.response.CreateTaskRes;
import com.education.education.goal.task.entities.Task;
import com.education.education.goal.task.mappers.TaskMapper;
import com.education.education.goal.task.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public CreateTaskRes createTask(
            CreateTaskReq request,
            Goal goal
    ){
        Task newTask = taskMapper.toTask(request);
        newTask.setGoal(goal);

        return taskMapper.toCreateTaskRes(taskRepository.save(newTask));
    }
}
