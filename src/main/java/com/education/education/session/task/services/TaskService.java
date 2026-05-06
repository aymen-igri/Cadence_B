package com.education.education.session.task.services;

import com.education.education.session.task.dto.request.CreateTaskReq;
import com.education.education.session.task.dto.request.UpdateTaskReq;
import com.education.education.session.task.dto.response.CreateTaskRes;
import com.education.education.session.task.entities.Task;
import com.education.education.session.task.mappers.TaskMapper;
import com.education.education.session.task.repository.TaskRepository;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
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
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;


    public CreateTaskRes createTask(
            UserDetails mainUser,
            CreateTaskReq request,
            UUID weeklySessionId
    ){
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(weeklySessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to create a task for this weekly session plan");
        }

        Task newTask = taskMapper.toTask(request, weeklySessionPlan);
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

        if (!task.getWeeklySessionPlan().getUser().getId().equals(user.getId())) {
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

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to view tasks for this goal");
        }

        List<Task> tasks = taskRepository.findByWeeklySessionPlan(weeklySessionPlan);

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

        WeeklySessionPlan weeklySessionPlan = task.getWeeklySessionPlan();
        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }
}
