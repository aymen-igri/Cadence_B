package com.education.education.user.user.dto.response;

import java.util.UUID;

import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;

public record UserSearchResponse(
    UUID id,
    String firstName,
    String lastName,
    EGender gender,
    String email,
    String phone,
    EStatus status) {
}
