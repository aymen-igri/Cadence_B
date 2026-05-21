package com.education.education.groups.DTO.response;

public record ChartGroupsForUserRes(
    String groupName,
    Integer activityRecord // postsCount + messagesCounts
) {
}
