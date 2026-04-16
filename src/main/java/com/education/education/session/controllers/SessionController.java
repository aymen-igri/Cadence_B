package com.education.education.session.controllers;

import com.education.education.session.dto.request.CreateSessionReq;
import com.education.education.session.dto.request.GenerationSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.GenerationSessionRes;
import com.education.education.session.services.GenerationService;
import com.education.education.session.weeklySessionPlan.services.WeeklySessionPlanService;
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
@RequestMapping("/session")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
public class SessionController {

    private final WeeklySessionPlanService weeklySessionPlanService;
    private final GenerationService generationService;

    @PostMapping("/create")
    public ResponseEntity<CreateSessionRes> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSessionReq sessionReq
    ){
        return ResponseEntity.ok(weeklySessionPlanService.createWeeklySessionPlan(userDetails, sessionReq.weeklySession(), sessionReq.subSessions()));
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerationSessionRes> generateSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GenerationSessionReq sessionReq
    ){
        return ResponseEntity.ok(generationService.generateSession(sessionReq, userDetails));
    }
}
