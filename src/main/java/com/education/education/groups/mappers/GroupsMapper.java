package com.education.education.groups.mappers;

import org.springframework.stereotype.Component;

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
}
