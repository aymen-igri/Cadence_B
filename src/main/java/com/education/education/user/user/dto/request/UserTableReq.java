package com.education.education.user.user.dto.request;

public record UserTableReq(
    UserSearchRequest request,
    Integer page,
    Integer size) {
}
