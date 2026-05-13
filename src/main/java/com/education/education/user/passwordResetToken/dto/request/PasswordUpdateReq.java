package com.education.education.user.passwordResetToken.dto.request;

import com.education.education.auth.enums.EMfaType;
import jakarta.validation.constraints.NotNull;

public record PasswordUpdateReq(
        @NotNull(message = "old password is required")
        String oldPassword,

        @NotNull(message = "new password is required")
        String newPassword,

        String code,

        EMfaType type
) {
}
