package com.education.education.groups.controllers;

import com.education.education.groups.DTO.request.CreateGroupRequest;
import com.education.education.groups.DTO.response.GroupMemberResponse;
import com.education.education.groups.DTO.response.GroupResponse;
import com.education.education.groups.services.GroupService;
import com.education.education.user.user.wrapper.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/all")
    public ResponseEntity<List<GroupResponse>> getAllGroups(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<GroupResponse> response = groupService.getAllGroups(userDetails.user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupResponse response = groupService.createGroup(request, userDetails.user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<GroupMemberResponse> members = groupService.getGroupMembers(groupId, userDetails.user.getId());
        return ResponseEntity.ok(members);
    }
}
