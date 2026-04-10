package com.education.education.goal.controllers;

import com.education.education.goal.dto.request.CreateGoalRequest;
import com.education.education.goal.dto.response.CreateGoalResponse;
import com.education.education.goal.goal.services.GoalService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/goal")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/create")
    public ResponseEntity<CreateGoalResponse> createGoal(
            @AuthenticationPrincipal UserDetails mainUser,
            @Valid @RequestBody CreateGoalRequest request
    ){
        return ResponseEntity.ok(goalService.createGoal(mainUser, request.goal(), request.tasks()));
    }
}
