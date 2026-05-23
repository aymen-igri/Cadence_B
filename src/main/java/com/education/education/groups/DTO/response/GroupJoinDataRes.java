package com.education.education.groups.DTO.response;

public record GroupJoinDataRes(
    Integer pendingReq,
    Integer acceptedReq,
    Integer rejectedReq) {
}
