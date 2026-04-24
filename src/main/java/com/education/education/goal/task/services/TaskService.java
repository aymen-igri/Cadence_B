package com.education.education.goal.task.services;

import com.education.education.goal.entities.Goal;
import com.education.education.goal.repositories.GoalRepository;
import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.dto.request.UpdateTaskReq;
import com.education.education.goal.task.dto.response.CreateTaskRes;
import com.education.education.goal.task.entities.Task;
import com.education.education.goal.task.mappers.TaskMapper;
import com.education.education.goal.task.repository.TaskRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public CreateTaskRes createTask(
            UserDetails mainUser,
            CreateTaskReq request,
            UUID goalId
    ){
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to create a task for this goal");
        }

        Task newTask = taskMapper.toTask(request, goal);
        Task savedTask = taskRepository.save(newTask);

        return taskMapper.toCreateTaskRes(savedTask);
    }

    public CreateTaskRes updateTask(
            UUID taskId,
            UpdateTaskReq request,
            UserDetails mainUser
    ){
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getGoal().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to update this task");
        }

        taskMapper.updateTaskFromReq(request, task);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toCreateTaskRes(savedTask);
    }

    public List<CreateTaskRes> getAllTasks(
            UserDetails mainUser,
            UUID goalId
    ){
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to view tasks for this goal");
        }

        List<Task> tasks = taskRepository.findByGoal(goal);

        return tasks.stream()
                .map(taskMapper::toCreateTaskRes)
                .toList();
    }

    public void deleteTask(UUID taskId, UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Goal goal = task.getGoal();
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }
}
