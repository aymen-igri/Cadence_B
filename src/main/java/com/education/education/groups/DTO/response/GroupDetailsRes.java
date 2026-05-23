package com.education.education.groups.DTO.response;

import java.util.List;
import java.util.UUID;

import com.education.education.groups.enums.GroupPrivacy;

public record GroupDetailsRes(
    UUID id,
    String name,
    String description,
    GroupPrivacy privacyLevel,
    GroupJoinDataRes joinReqData,
    List<GroupMembersDataRes> members,
    List<GroupMessageActivityDataRes> messageActivity) {
}
