package com.education.education.session.services;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilityPlan.repositories.AvailabilityPlanRepository;
import com.education.education.availability.availabilitySlot.entities.AvailabilitySlot;
import com.education.education.availability.availabilitySlot.repositories.AvailabilitySlotRepository;
import com.education.education.goal.goal.entities.Goal;
import com.education.education.goal.goal.repositories.GoalRepository;
import com.education.education.session.dto.middle.EngineResult;
import com.education.education.session.dto.middle.GenerationData;
import com.education.education.session.dto.request.GenerationSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.GenerationSessionRes;
import com.education.education.session.mappers.GenerationMapper;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.EGenerationType;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.subject.entities.Subject;
import com.education.education.subject.repositories.SubjectRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class GenerationService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final SubjectRepository subjectRepository;
    private final AvailabilityPlanRepository availabilityPlanRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final GenerationMapper generationMapper;

    public GenerationSessionRes generateSession(
            GenerationSessionReq req,
            UserDetails mainUser
    ){
        User user = userRepository.findByUsername(mainUser.getUsername());

        GenerationData data = generationMapper.toGenerationData(req);
        List<Goal> goals = data.goals();
        AvailabilityPlan availabilityPlan = data.availabilityPlan();
        LocalDateTime weekStartDate = data.weekStartDate();

        List<Subject> subjects = subjectRepository.findAllById(
                goals.stream()
                        .map(Goal::getSubject)
                        .map(Subject::getId)
                        .toList()
        );

        WeeklySessionPlan newWeeklySessionPlan = new WeeklySessionPlan();
        newWeeklySessionPlan.setUser(user);
        newWeeklySessionPlan.setStartTime(weekStartDate);
        newWeeklySessionPlan.setSessionStatus(ESessionStatus.PENDING);
        newWeeklySessionPlan.setPlanStatus(EPlanStatus.DRAFT);
        newWeeklySessionPlan.setGenerationType(EGenerationType.AUTO_GENERATED);
        newWeeklySessionPlan.setAvailabilityPlan(availabilityPlan);

        WeeklySessionPlan savedPlan = weeklySessionPlanRepository.save(newWeeklySessionPlan);

        List<AvailabilitySlot> availabilitySlots = availabilitySlotRepository.findAllByAvailabilityPlan(availabilityPlan);

        EngineResult engineResult = FFD_Alg(
                goals,
                availabilitySlots,
                newWeeklySessionPlan
        );

        savedPlan.setPenaltyPoints(engineResult.penaltyPoints());
        weeklySessionPlanRepository.save(savedPlan);

        List<CreateSubSessionRes> subSessionRes = new ArrayList<>();
        engineResult.subSessions().forEach(subSession -> {;
            subSessionRes.add(
                    new CreateSubSessionRes(
                            subSession.getId(),
                            subSession.getDayOfWeek(),
                            subSession.getStartTime(),
                            subSession.getEndTime(),
                            subSession.getSubSessionStatus(),
                            subSession.getSubject().getId(),
                            subSession.getSubject().getName()
                    )
            );
        });

        return generationMapper.toGeneratedSession(
                new CreateSessionRes(
                        new CreateWeeklySessionRes(
                                savedPlan.getId(),
                                savedPlan.getStartTime(),
                                savedPlan.getSessionStatus()
                        ),
                        subSessionRes
                ),
                savedPlan.getPlanStatus(),
                savedPlan.getPenaltyPoints()
        );
    }

    public EngineResult FFD_Alg(
            List<Goal> goals,
            List<AvailabilitySlot> availabilitySlots,
            WeeklySessionPlan plan
    ){
        return null;
    }
}
