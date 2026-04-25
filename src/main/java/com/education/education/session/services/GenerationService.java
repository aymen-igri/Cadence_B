package com.education.education.session.services;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilitySlot.entities.AvailabilitySlot;
import com.education.education.availability.availabilitySlot.repositories.AvailabilitySlotRepository;
import com.education.education.goal.entities.Goal;
import com.education.education.session.dto.middle.EngineResult;
import com.education.education.session.dto.middle.GenerationData;
import com.education.education.session.dto.request.GenerationSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.GenerationSessionRes;
import com.education.education.session.mappers.GenerationMapper;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.EGenerationAlgoType;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@Transactional
@AllArgsConstructor
public class GenerationService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final SubSessionRepository subSessionRepository;
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

        if (req.usePriority()){
            newWeeklySessionPlan.setGenerationAlgoType(EGenerationAlgoType.PRIORITY_FIRST);
        } else {
            newWeeklySessionPlan.setGenerationAlgoType(EGenerationAlgoType.DENSITY_FIRST);
        }

        WeeklySessionPlan savedPlan = weeklySessionPlanRepository.save(newWeeklySessionPlan);

        List<AvailabilitySlot> availabilitySlots = availabilitySlotRepository.findAllByAvailabilityPlan(availabilityPlan);

        EngineResult engineResult = FFD_Alg(
                goals,
                availabilitySlots,
                newWeeklySessionPlan,
                req.usePriority()
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
                                savedPlan.getTitle(),
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
            WeeklySessionPlan plan,
            boolean usePriority
    ){

        // sorting section based on the user choise
        if (usePriority) {
            // sorting goals by priority then by duration
            goals.sort(Comparator.comparing((Goal g) -> g.getSubject().getPriority())
                    .thenComparing(Goal::getTargetHoursPerWeek, Comparator.reverseOrder()));
        }else {
            // sorting goals by duration
            goals.sort(Comparator.comparing(Goal::getTargetHoursPerWeek).reversed());
        }


        // sorting availability slots by date
        availabilitySlots.sort(Comparator.comparing(AvailabilitySlot::getDayOfWeek)
                .thenComparing(AvailabilitySlot::getStartTime));

        List<SubSession> generatedSubSessions = new ArrayList<>();
        long totalPenaltyPoints = 0;

        int currentGoalIdx = 0;
        int currentASlotIdx = 0;

        long currentGoalMinutes = (currentGoalIdx < goals.size()) ? (long)(goals.get(currentGoalIdx).getTargetHoursPerWeek() * 60) : 0;
        long currentASlotMinutes = (currentASlotIdx < availabilitySlots.size()) ? Duration.between(
                availabilitySlots.get(currentASlotIdx).getStartTime(),
                availabilitySlots.get(currentASlotIdx).getEndTime()
        ).toMinutes() : 0;

        while(currentGoalIdx < goals.size() && currentASlotIdx < availabilitySlots.size()){
            Goal goal = goals.get(currentGoalIdx);
            AvailabilitySlot aSlot = availabilitySlots.get(currentASlotIdx);

            long fillAmount = Math.min(currentGoalMinutes, currentASlotMinutes);

            if (fillAmount > 0){
                SubSession newSSision = new SubSession();
                newSSision.setWeeklySessionPlan(plan);
                newSSision.setDayOfWeek(aSlot.getDayOfWeek());
                newSSision.setSubject(goal.getSubject());
                newSSision.setGoal(goal);
                newSSision.setSubSessionStatus(ESubSessionStatus.PENDING);

                long totalSlotDuration = Duration.between(aSlot.getStartTime(), aSlot.getEndTime()).toMinutes();
                long offset = totalSlotDuration - currentASlotMinutes;

                newSSision.setStartTime(aSlot.getStartTime().plusMinutes(offset));
                newSSision.setEndTime(newSSision.getStartTime().plusMinutes(fillAmount));

                generatedSubSessions.add(subSessionRepository.save(newSSision));

                currentGoalMinutes -= fillAmount;
                currentASlotMinutes -= fillAmount;
            }

            if (currentGoalMinutes <= 0){
                currentGoalIdx++;
                if (currentGoalIdx < goals.size()) {
                    currentGoalMinutes = (long)(goals.get(currentGoalIdx).getTargetHoursPerWeek() * 60);
                }
            }

            if (currentASlotMinutes <= 0){
                currentASlotIdx++;
                if (currentASlotIdx < availabilitySlots.size()){
                    currentASlotMinutes = Duration.between(
                            availabilitySlots.get(currentASlotIdx).getStartTime(),
                            availabilitySlots.get(currentASlotIdx).getEndTime()
                    ).toMinutes();
                }
            }
        }

        while (currentGoalIdx < goals.size()){
            totalPenaltyPoints += currentGoalMinutes;
            currentGoalIdx++;
            if (currentGoalIdx < goals.size()) {
                currentGoalMinutes = (long)(goals.get(currentGoalIdx).getTargetHoursPerWeek() * 60);
            }
        }

        return new EngineResult(
                generatedSubSessions,
                totalPenaltyPoints
        );
    }
}
