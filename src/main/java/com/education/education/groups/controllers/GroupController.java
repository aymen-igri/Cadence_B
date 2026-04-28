package com.education.education.groups.controllers;

import com.education.education.groups.DTO.request.CreateGroupRequest;
import com.education.education.groups.DTO.request.UpdateGroupRequest;
import com.education.education.groups.DTO.response.GroupMemberResponse;
import com.education.education.groups.DTO.response.GroupResponse;
import com.education.education.groups.DTO.response.JoinRequestResponse;
import com.education.education.groups.services.GroupService;
import com.education.education.user.user.wrapper.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/all")
    public ResponseEntity<List<GroupResponse>> getAllGroups(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<GroupResponse> response = groupService.getAllGroups(userDetails.user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroupById(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupResponse response = groupService.getGroupById(groupId, userDetails.user.getId());
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

    @PostMapping("/{groupId}/join")
    public ResponseEntity<GroupMemberResponse> joinPublicGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupMemberResponse response = groupService.joinPublicGroup(groupId, userDetails.user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/join-request")
    public ResponseEntity<JoinRequestResponse> sendJoinRequest(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        JoinRequestResponse response = groupService.sendJoinRequest(groupId, userDetails.user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}/requests")
    public ResponseEntity<List<JoinRequestResponse>> getPendingRequests(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<JoinRequestResponse> requests = groupService.getPendingRequests(groupId, userDetails.user.getId());
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{groupId}/requests/{targetUserId}/approve")
    public ResponseEntity<GroupMemberResponse> approveJoinRequest(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupMemberResponse response = groupService.approveJoinRequest(groupId, targetUserId, userDetails.user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/requests/{targetUserId}/reject")
    public ResponseEntity<Void> rejectJoinRequest(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.rejectJoinRequest(groupId, targetUserId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable UUID groupId,
            @RequestBody UpdateGroupRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupResponse response = groupService.updateGroup(groupId, request, userDetails.user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.deleteGroup(groupId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupId}/transfer/{targetUserId}")
    public ResponseEntity<Void> transferOwnership(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.transferOwnership(groupId, targetUserId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.leaveGroup(groupId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/members/{targetUserMemberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserMemberId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.removeMember(groupId, targetUserMemberId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupId}/members/{targetUserMemberShipId}/promote")
    public ResponseEntity<Void> promoteMember(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserMemberShipId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.promoteMember(groupId, targetUserMemberShipId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupId}/members/{targetUserMemberShipId}/demote")
    public ResponseEntity<Void> demoteAdmin(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserMemberShipId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        groupService.demoteAdmin(groupId, targetUserMemberShipId, userDetails.user.getId());
        return ResponseEntity.noContent().build();
    }
}
