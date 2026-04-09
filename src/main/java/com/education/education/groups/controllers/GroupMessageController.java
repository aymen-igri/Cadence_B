package com.education.education.groups.controllers;

import com.education.education.groups.DTO.request.SendGroupMessageRequest;
import com.education.education.groups.DTO.response.GroupMessageResponse;
import com.education.education.groups.services.GroupMessageService;
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
@RequestMapping("/groups/{groupId}/messages")
@RequiredArgsConstructor
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    @PostMapping
    public ResponseEntity<GroupMessageResponse> sendMessage(
            @PathVariable UUID groupId,
            @Valid @RequestBody SendGroupMessageRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        GroupMessageResponse response = groupMessageService.sendMessage(groupId, userDetails.user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupMessageResponse>> getChatHistory(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<GroupMessageResponse> messages = groupMessageService.getGroupChatHistory(groupId, userDetails.user.getId());
        return ResponseEntity.ok(messages);
    }
}
