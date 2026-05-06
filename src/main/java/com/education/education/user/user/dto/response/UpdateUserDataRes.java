package com.education.education.user.user.dto.response;

import com.education.education.user.user.enums.EGender;

import java.util.UUID;

public record UpdateUserDataRes(
        UUID id,
        String firstName,
        String lastName,
        EGender gender,
        String phone
) {
}
