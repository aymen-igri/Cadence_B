package com.education.education.groups.mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.education.education.groups.DTO.request.GroupSearchRequest;
import com.education.education.groups.DTO.request.GroupsTableReq;
import com.education.education.groups.DTO.response.ChartGroupsForUserRes;
import com.education.education.groups.DTO.response.GroupDetailsRes;
import com.education.education.groups.DTO.response.GroupJoinDataRes;
import com.education.education.groups.DTO.response.GroupMembersDataRes;
import com.education.education.groups.DTO.response.GroupMessageActivityDataRes;
import com.education.education.groups.DTO.response.TopActiveGroupsResponse;
import com.education.education.groups.enums.GroupPrivacy;
import com.education.education.groups.enums.GroupRole;

@Component
public class GroupsMapper {

  public TopActiveGroupsResponse toTopActiveGroupsResponse(
      String name,
      GroupPrivacy groupPrivacy,
      Integer membersCount) {
    return new TopActiveGroupsResponse(
        name,
        groupPrivacy,
        membersCount);
  }

  public ChartGroupsForUserRes toChartGroupsForUserRes(
      String groupName,
      Long personalGroupActivity) {
    return new ChartGroupsForUserRes(
        groupName,
        personalGroupActivity);
  }

  public GroupSearchRequest toGroupSearchRequest(
      String name,
      GroupPrivacy privacyLevel) {
    return new GroupSearchRequest(
        name,
        privacyLevel);
  }

  public GroupsTableReq toGroupsTableReq(
      GroupSearchRequest request,
      Integer page,
      Integer size) {
    return new GroupsTableReq(
        request,
        page,
        size);
  }

  public GroupJoinDataRes toGroupJoinbuttonMapper(
      Integer pendingReq,
      Integer acceptedReq,
      Integer rejectedReq) {
    return new GroupJoinDataRes(
        pendingReq,
        acceptedReq,
        rejectedReq);
  }

  public GroupMembersDataRes toGroupMembersDataRes(
      UUID id,
      String firstName,
      String lastName,
      GroupRole role,
      LocalDateTime joinedAt) {
    return new GroupMembersDataRes(
        id,
        firstName,
        lastName,
        role,
        joinedAt != null ? joinedAt.toLocalDate() : LocalDate.now());
  }

  public GroupMessageActivityDataRes toGroupMessageActivityDataRes(
      LocalDate date,
      Long messageCount) {
    return new GroupMessageActivityDataRes(
        date,
        messageCount);
  }

  public GroupDetailsRes toGroupDetailsRes(
      UUID id,
      String name,
      String description,
      GroupPrivacy privacyLevel,
      GroupJoinDataRes joinReqData,
      java.util.List<GroupMembersDataRes> members,
      java.util.List<GroupMessageActivityDataRes> messageActivity) {
    return new GroupDetailsRes(
        id,
        name,
        description,
        privacyLevel,
        joinReqData,
        members,
        messageActivity);
  }
}
