package com.education.education.session.subSession.services;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.mappers.SubSessionMapper;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.subject.mappers.SubjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class SubSessionService {

    private final SubSessionRepository subSessionRepository;
    private final SubSessionMapper subSessionMapper;

    public CreateSubSessionRes createSubSession(
            CreateSubSessionReq request,
            WeeklySessionPlan weeklySessionPlan
    ){
        SubSession newSubSession = subSessionMapper.toSubSession(request);
        newSubSession.setWeeklySessionPlan(weeklySessionPlan);

        return subSessionMapper.toCreateSubSessionRes(subSessionRepository.save(newSubSession));
    }
}
