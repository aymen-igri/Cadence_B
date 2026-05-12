package com.education.education.user.passwordResetToken.dto.request;

import jakarta.validation.constraints.NotNull;

public record PasswordResetReq(

        @NotNull(message = "token is required")
        String token,

        @NotNull(message = "new password is required")
        String newPassword
) {
}
