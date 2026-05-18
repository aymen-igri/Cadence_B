package com.education.education.groups.DTO.response;

import java.util.List;

public record PagedMessageResponse(
        List<GroupMessageResponse> messages,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasMore) {
}
