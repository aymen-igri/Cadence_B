package com.education.education.groups.DTO.response;

public record ChartGroupsForUserRes(
    String groupName,
    Long activityRecord // postsCount + messagesCounts
) {
}
