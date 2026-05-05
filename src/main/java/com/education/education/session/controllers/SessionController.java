package com.education.education.session.controllers;

import com.education.education.session.dto.request.CreateSessionReq;
import com.education.education.session.dto.request.GenerationSessionReq;
import com.education.education.session.dto.request.UpdateSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.GenerationSessionRes;
import com.education.education.session.services.GenerationService;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.services.WeeklySessionPlanService;
import com.education.education.session.subSession.dto.request.UpdateSubSessionStatusReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/session")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

    private final WeeklySessionPlanService weeklySessionPlanService;
    private final GenerationService generationService;

    @PostMapping("/create")
    public ResponseEntity<CreateSessionRes> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSessionReq sessionReq) {
        return ResponseEntity.ok(weeklySessionPlanService.createWeeklySessionPlan(userDetails,
                sessionReq.weeklySession(), sessionReq.subSessions()));
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerationSessionRes> generateSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GenerationSessionReq sessionReq) {
        return ResponseEntity.ok(generationService.generateSession(sessionReq, userDetails));
    }

    @PatchMapping("/approve/{sessionId}")
    public ResponseEntity<CreateSessionRes> approveSession(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(weeklySessionPlanService.approveSession(sessionId, userDetails));
    }

    @GetMapping("/all/{planStatus}")
    public ResponseEntity<List<CreateSessionRes>> getAllWeeklySessionPlans(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable EPlanStatus planStatus) {
        return ResponseEntity.ok(weeklySessionPlanService.getAllWeeklySessionPlans(userDetails, planStatus));
    }

    @PatchMapping("/update/{sessionId}")
    public ResponseEntity<CreateSessionRes> updateWeeklySessionPlan(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateSessionReq request) {
        return ResponseEntity.ok(weeklySessionPlanService.updateWeeklySessionPlan(sessionId, request, userDetails));
    }
    
    @DeleteMapping("/delete/{sessionId}")
    public ResponseEntity<Void> deleteWeeklySessionPlan(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        weeklySessionPlanService.deleteWeeklySessionPlan(sessionId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{weeklySessionId}/sub-sessions/{subSessionId}/status")
    public ResponseEntity<?> updateSubSessionStatus(
            @PathVariable UUID weeklySessionId,
            @PathVariable UUID subSessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateSubSessionStatusReq request) {
        try {
            CreateSubSessionRes res = weeklySessionPlanService.updateSubSessionStatus(weeklySessionId, subSessionId,
                    request, userDetails);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
