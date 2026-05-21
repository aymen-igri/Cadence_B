package com.education.education.auth.deo.responses;

import java.time.LocalDateTime;

import com.education.education.auth.enums.EMfaType;

public record MfaActivityRes(
    String username,
    EMfaType type,
    int attempts,
    LocalDateTime time,
    boolean isUsed) {
}
