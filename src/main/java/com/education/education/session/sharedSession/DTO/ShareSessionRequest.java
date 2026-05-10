package com.education.education.session.sharedSession.DTO;

import java.util.UUID;

import com.education.education.session.sharedSession.enums.SharedSessionPermission;

public record ShareSessionRequest(
                UUID sessionId,
                UUID groupId,
                SharedSessionPermission permission) {

}
