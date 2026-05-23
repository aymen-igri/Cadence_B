package com.education.education.groups.DTO.request;

public record GroupsTableReq(
    GroupSearchRequest groupData,
    Integer page,
    Integer size) {
}
