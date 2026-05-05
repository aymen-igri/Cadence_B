package com.education.education.user.user.dto.response;

import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;

import java.util.List;
import java.util.UUID;

public record UserProfileRes(
        UUID id,
        String firstname,
        String lastName,
        EGender gender,
        String email,
        String phone,
        boolean isTotpEnabled,
        EStatus status,
        String profilePic,
        String role
) {
}
