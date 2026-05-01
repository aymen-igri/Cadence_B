package com.education.education.availability.controllers;

import com.education.education.availability.availabilityPlan.dto.response.CreateAvailabilityPlanRes;
import com.education.education.availability.availabilityPlan.services.AvailabilityPlanService;
import com.education.education.availability.dto.request.CreateAvailabilityRequest;
import com.education.education.availability.dto.request.UpdateAvailabilitySlotsRequest;
import com.education.education.availability.dto.response.CreateAvailabilityRes;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/availability")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AvailabilityController {
    
    private final AvailabilityPlanService availabilityPlanService;
    
    @PostMapping("/create")
    public ResponseEntity<CreateAvailabilityRes> createAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAvailabilityRequest availabilityRequest
    ){
        return ResponseEntity.ok(availabilityPlanService.createAvailabilityPlan(userDetails, availabilityRequest.availabilityPlan(), availabilityRequest.slotsReq()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CreateAvailabilityPlanRes>> getAllAvailabilityPlans(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(availabilityPlanService.getAllAvailabilityPlans(userDetails));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<CreateAvailabilityRes> getAvailabilityPlan(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable UUID planId
    ){ 
        return ResponseEntity.ok(availabilityPlanService.getAvailabilityPlan(userDetails, planId));
    }

    @PutMapping("/{planId}/slots")
    public ResponseEntity<CreateAvailabilityRes> updateAvailabilitySlots(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID planId,
            @Valid @RequestBody UpdateAvailabilitySlotsRequest request
    ) {
        return ResponseEntity.ok(availabilityPlanService.updateAvailabilitySlots(userDetails, planId, request));
    }
    
}
