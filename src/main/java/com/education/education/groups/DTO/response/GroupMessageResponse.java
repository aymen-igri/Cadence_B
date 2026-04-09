package com.education.education.groups.DTO.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupMessageResponse(
        UUID id,
        UUID groupId,
        UUID senderId,
        String senderFirstName,
        String senderLastName,
        String content,
        LocalDateTime sentAt
) {
}
