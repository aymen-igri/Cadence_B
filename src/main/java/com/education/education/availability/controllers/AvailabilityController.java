package com.education.education.availability.controllers;

import com.education.education.availability.availabilityPlan.services.AvailabilityPlanService;
import com.education.education.availability.dto.request.CreateAvailabilityRequest;
import com.education.education.availability.dto.response.CreateAvailabilityRes;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/availability")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
public class AvailabilityController {
    
    private final AvailabilityPlanService availabilityPlanService;
    
    @PostMapping("/create")
    public ResponseEntity<CreateAvailabilityRes> createAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAvailabilityRequest availabilityRequest
    ){
        return ResponseEntity.ok(availabilityPlanService.createAvailabilityPlan(userDetails, availabilityRequest.availabilityPlan(), availabilityRequest.slotsReq()));
    }
}
