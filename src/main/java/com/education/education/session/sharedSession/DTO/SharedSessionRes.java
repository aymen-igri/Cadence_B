package com.education.education.session.sharedSession.DTO;

import com.education.education.session.sharedSession.enums.SharedSessionPermission;
import java.time.LocalDateTime;
import java.util.UUID;

public record SharedSessionRes(
        UUID sharedSessionId,
        UUID sessionId,
        String sessionTitle,
        UUID groupId,
        String groupName,
        LocalDateTime sharedAt,
        UUID sharedByUserId,
        String sharedByUsername,
        SharedSessionPermission permission) {
}
