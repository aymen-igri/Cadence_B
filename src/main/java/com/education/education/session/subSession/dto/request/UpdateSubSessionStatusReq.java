package com.education.education.session.subSession.dto.request;

import com.education.education.session.subSession.enums.ESubSessionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSubSessionStatusReq(
        @NotNull(message = "Status is required")
        ESubSessionStatus status
) {
}
