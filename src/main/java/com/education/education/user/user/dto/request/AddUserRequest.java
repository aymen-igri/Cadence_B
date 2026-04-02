package com.education.education.user.user.dto.request;

import com.education.education.user.user.enums.EGender;
import jakarta.validation.constraints.NotBlank;

public record AddUserRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Gender is required")
        EGender gender,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}
