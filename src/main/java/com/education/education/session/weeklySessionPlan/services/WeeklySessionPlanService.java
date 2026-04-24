package com.education.education.session.weeklySessionPlan.services;

import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.services.SubSessionService;
import com.education.education.session.weeklySessionPlan.dto.request.CreateWeeklySessionReq;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.mappers.WeeklySessionPlanMapper;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class WeeklySessionPlanService {

    private final UserRepository userRepository;
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final WeeklySessionPlanMapper weeklySessionPlanMapper;
    private final SubSessionService subSessionPlanService;

    public CreateSessionRes createWeeklySessionPlan(
            UserDetails mainUser,
            CreateWeeklySessionReq sessionReq,
            List<CreateSubSessionReq> subSessionsReq
    ){
        WeeklySessionPlan session = weeklySessionPlanMapper.toWeeklySessionPlan(sessionReq);
        session.setUser(userRepository.findByUsername(mainUser.getUsername()));
        WeeklySessionPlan savedSession = weeklySessionPlanRepository.save(session);

        List<CreateSubSessionRes> createdSubSessions = new ArrayList<>();

        for(CreateSubSessionReq subSessionReq:subSessionsReq){
            createdSubSessions.add(subSessionPlanService.createSubSession(subSessionReq, savedSession));
        }

        return new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(savedSession),
                createdSubSessions
        );
    }
}
