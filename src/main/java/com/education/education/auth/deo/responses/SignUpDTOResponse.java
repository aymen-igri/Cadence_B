package com.education.education.auth.deo.responses;

import com.education.education.user.role.dto.response.AddRoleToUserResponse;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.enums.EGender;

import java.time.LocalDateTime;
import java.util.UUID;

public record SignUpDTOResponse(
        AddUserResponse userData,
        AddRoleToUserResponse userRole
) {
}
