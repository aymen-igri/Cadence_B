package com.education.education.groups.mappers;

import org.springframework.stereotype.Component;

import com.education.education.groups.DTO.request.GroupSearchRequest;
import com.education.education.groups.DTO.request.GroupsTableReq;
import com.education.education.groups.DTO.response.ChartGroupsForUserRes;
import com.education.education.groups.DTO.response.TopActiveGroupsResponse;
import com.education.education.groups.enums.GroupPrivacy;

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
}
