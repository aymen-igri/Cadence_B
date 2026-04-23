package com.education.education.goal.controllers;


import com.education.education.goal.dto.request.CreateGoalReq;
import com.education.education.goal.dto.response.CreateGoalRes;
import com.education.education.goal.services.GoalService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/goal")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/create/{subjectId}")
    public ResponseEntity<CreateGoalRes> createGoal(
            @AuthenticationPrincipal UserDetails mainUser,
            @PathVariable UUID subjectId,
            @Valid @RequestBody CreateGoalReq request
    ){
        return ResponseEntity.ok(goalService.createGoal(mainUser, request, subjectId));
    }

    @GetMapping("/all/{subjectId}")
    public ResponseEntity<List<CreateGoalRes>> getAllGoals(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable UUID subjectId
    ){
        return ResponseEntity.ok(goalService.getAllGoals(userDetails, subjectId));
    }
    
}
