package com.education.education.groups.DTO.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupMessageResponse(
                UUID id,
                UUID groupId,
                UUID senderId,
                String groupName,
                String senderFirstName,
                String senderLastName,
                String senderProfilePic,
                String content,
                LocalDateTime sentAt) {
}
