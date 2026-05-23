package com.education.education.groups.DTO.response;

import java.time.LocalDate;

public record GroupMessageActivityDataRes(
    LocalDate date,
    Long messageCount) {
}
