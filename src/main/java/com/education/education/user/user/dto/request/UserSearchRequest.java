package com.education.education.user.user.dto.request;

import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;

public record UserSearchRequest(
    String firstName,
    String lastName,
    EGender gender,
    String email,
    String phone,
    EStatus status) {
}
