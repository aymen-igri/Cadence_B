package com.education.education.user.user.dto.request;

import com.education.education.user.user.enums.EGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateUserDataReq(
        @NotBlank(message = "First name is required")
        @Size(min = 3, message = "First name should contain at least 2 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, message = "Last name should contain at least 2 characters")
        String lastName,

        @NotNull(message = "Gender is required")
        EGender gender,

        @NotBlank(message = "Phone is required")
        @Size(min = 3, message = "Last name should contain at least 3 characters")
        String phone
) {
}
