package com.education.education.subject.dto.request;

import com.education.education.subject.enums.EPriority;
public record UpdateSubjectReq(
        String name,
        EPriority priority,
        String description
) {
}
