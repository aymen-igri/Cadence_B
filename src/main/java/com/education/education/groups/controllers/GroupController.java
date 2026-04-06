package com.education.education.groups.controllers;

import com.education.education.groups.DTO.request.CreateGroupRequest;
import com.education.education.groups.DTO.response.GroupResponse;
import com.education.education.groups.services.GroupService;
import com.education.education.user.user.wrapper.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupResponse response = groupService.createGroup(request, userDetails.user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
