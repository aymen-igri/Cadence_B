package com.education.education.user.admin.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.education.education.groups.repositories.GroupRepository;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.subject.repositories.SubjectRepository;
import com.education.education.user.admin.dto.res.Cards;
import com.education.education.user.admin.mappers.AdminMapper;
import com.education.education.user.user.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class AdminService {

  private final AdminMapper adminMapper;
  private final UserRepository userRepository;
  private final WeeklySessionPlanRepository weeklySessionPlanRepository;
  private final GroupRepository groupRepository;
  private final SubjectRepository subjectRepository;

  public Cards cards() {
    Number registredUserCount = userRepository.count();
    Number weeklySessionCount = weeklySessionPlanRepository.count();
    Number groupCount = groupRepository.count();
    Number subjectCount = subjectRepository.count();

    return adminMapper.toCards(registredUserCount, weeklySessionCount, groupCount, subjectCount);
  }
}
