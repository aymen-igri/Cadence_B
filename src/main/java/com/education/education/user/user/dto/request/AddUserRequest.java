package com.education.education.user.user.dto.request;

import com.education.education.user.user.enums.EGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddUserRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 3, message = "First name should contain at least 2 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, message = "Last name should contain at least 2 characters")
        String lastName,

        @NotNull(message = "Gender is required")
        EGender gender,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Phone is required")
        @Size(min = 3, message = "Last name should contain at least 3 characters")
        String phone,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}
