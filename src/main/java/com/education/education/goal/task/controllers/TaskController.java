package com.education.education.goal.task.controllers;

import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.dto.response.CreateTaskRes;
import com.education.education.goal.task.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/task")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
public class TaskController {
    
    private final TaskService taskService;

    @PostMapping("/create/{goalId}")
    public ResponseEntity<CreateTaskRes> createTask(
            @AuthenticationPrincipal UserDetails mainUser,
            @PathVariable UUID goalId,
            @Valid @RequestBody CreateTaskReq request
    ){
        return ResponseEntity.ok(taskService.createTask(mainUser, request, goalId));
    }

    @GetMapping("/all/{goalId}")
    public ResponseEntity<List<CreateTaskRes>> getAllTasks(
            @AuthenticationPrincipal UserDetails mainUser,
            @PathVariable UUID goalId
    ){
        return ResponseEntity.ok(taskService.getAllTasks(mainUser, goalId));
    }
}
