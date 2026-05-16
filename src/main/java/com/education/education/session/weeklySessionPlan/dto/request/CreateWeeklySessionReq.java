package com.education.education.session.weeklySessionPlan.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWeeklySessionReq(
                @NotNull(message = "Title is required") String title,

                @NotNull(message = "Week year is required") @Min(value = 2026, message = "Week year must be 2026 or later") Integer weekYear,

                @NotNull(message = "Week number is required") @Min(value = 1, message = "Week number must be at least 1") @Max(value = 53, message = "Week number must be at most 53") Integer weekNumber) {
}
