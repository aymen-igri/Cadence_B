package com.education.education.user.admin.mappers;

import org.springframework.stereotype.Component;

import com.education.education.user.admin.dto.res.Cards;

@Component
public class AdminMapper {

  public Cards toCards(
      Number registeredUserCount,
      Number weeklyPlanCount,
      Number groupCount,
      Number subjectCount) {
    return new Cards(
        registeredUserCount,
        weeklyPlanCount,
        groupCount,
        subjectCount);
  }
}
