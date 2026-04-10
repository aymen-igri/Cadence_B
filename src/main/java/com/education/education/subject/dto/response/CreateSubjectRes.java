package com.education.education.subject.dto.response;

import com.education.education.subject.enums.EPriority;
import com.education.education.user.user.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateSubjectRes(
        UUID id,
        String name,
        EPriority priority,
        String description,
        LocalDateTime createdAt
) {
}
