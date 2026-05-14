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
import com.education.education.session.weeklySessionPlan.services.WeeklySessionPlanService;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import com.education.education.exeption.PastWeekException;
import com.education.education.exeption.WeeklySessionAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class GenerationService {

    private final UserRepository userRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final SubSessionRepository subSessionRepository;
    private final GenerationMapper generationMapper;
    private final WeeklySessionPlanService weeklySessionPlanService;

    public GenerationSessionRes generateSession(
            GenerationSessionReq req,
            UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());

        GenerationData data = generationMapper.toGenerationData(req);
        List<Goal> goals = data.goals();
        AvailabilityPlan availabilityPlan = data.availabilityPlan();
        LocalDateTime weekStartDate = data.weekStartDate();
        int weekYear = weekStartDate.get(WeekFields.ISO.weekBasedYear());
        int weekNumber = weekStartDate.get(WeekFields.ISO.weekOfWeekBasedYear());

        validateWeekIsCurrentOrFuture(weekStartDate);

        if (weeklySessionPlanRepository.existsByUser_IdAndWeekYearAndWeekNumber(user.getId(), weekYear, weekNumber)) {
            throw new WeeklySessionAlreadyExistsException(weekYear, weekNumber);
        }

        WeeklySessionPlan newWeeklySessionPlan = new WeeklySessionPlan();
        newWeeklySessionPlan.setUser(user);
        newWeeklySessionPlan.setWeekYear(weekYear);
        newWeeklySessionPlan.setWeekNumber(weekNumber);
        newWeeklySessionPlan.setSessionStatus(ESessionStatus.UPCOMING);
        newWeeklySessionPlan.setPlanStatus(EPlanStatus.DRAFT);
        newWeeklySessionPlan.setGenerationType(EGenerationType.AUTO_GENERATED);
        newWeeklySessionPlan.setAvailabilityPlan(availabilityPlan);
        newWeeklySessionPlan.setTitle(req.title());

        if (req.usePriority()) {
            newWeeklySessionPlan.setGenerationAlgoType(EGenerationAlgoType.PRIORITY_FIRST);
        } else {
            newWeeklySessionPlan.setGenerationAlgoType(EGenerationAlgoType.DENSITY_FIRST);
        }

        WeeklySessionPlan savedPlan = weeklySessionPlanRepository.save(newWeeklySessionPlan);

        List<AvailabilitySlot> availabilitySlots = availabilitySlotRepository
                .findAllByAvailabilityPlan(availabilityPlan);

        EngineResult engineResult = FFD_Alg(
                goals,
                availabilitySlots,
                newWeeklySessionPlan,
                req.usePriority());

        validateGeneratedSubSessions(savedPlan, engineResult.subSessions());

        savedPlan.setPenaltyPoints(engineResult.penaltyPoints());
        weeklySessionPlanRepository.save(savedPlan);
        weeklySessionPlanService.deriveStatus(savedPlan);

        List<CreateSubSessionRes> subSessionRes = new ArrayList<>();
        engineResult.subSessions().forEach(subSession -> {
            ;
            subSessionRes.add(
                    new CreateSubSessionRes(
                            subSession.getId(),
                            subSession.getDayOfWeek(),
                            subSession.getStartTime(),
                            subSession.getEndTime(),
                            subSession.getSubSessionStatus(),
                            subSession.getSubject().getId(),
                            subSession.getSubject().getName()));
        });

        return generationMapper.toGeneratedSession(
                new CreateSessionRes(
                        new CreateWeeklySessionRes(
                                savedPlan.getId(),
                                savedPlan.getWeekYear(),
                                savedPlan.getWeekNumber(),
                                savedPlan.getTitle(),
                                savedPlan.getSessionStatus()),
                        subSessionRes),
                savedPlan.getPlanStatus(),
                savedPlan.getPenaltyPoints());
    }

    private void validateWeekIsCurrentOrFuture(LocalDateTime weekStartDate) {
        WeekFields weekFields = WeekFields.ISO;
        LocalDate today = LocalDate.now();
        int currentWeekYear = today.get(weekFields.weekBasedYear());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());

        int targetWeekYear = weekStartDate.get(weekFields.weekBasedYear());
        int targetWeekNumber = weekStartDate.get(weekFields.weekOfWeekBasedYear());

        boolean isPastWeek = targetWeekYear < currentWeekYear
                || (targetWeekYear == currentWeekYear && targetWeekNumber < currentWeekNumber);

        if (isPastWeek) {
            throw new PastWeekException(targetWeekYear, targetWeekNumber);
        }
    }

    private void validateGeneratedSubSessions(WeeklySessionPlan weeklySessionPlan,
            List<SubSession> generatedSubSessions) {
        for (int i = 0; i < generatedSubSessions.size(); i++) {
            SubSession current = generatedSubSessions.get(i);
            for (int j = i + 1; j < generatedSubSessions.size(); j++) {
                SubSession candidate = generatedSubSessions.get(j);
                if (!current.getDayOfWeek().equals(candidate.getDayOfWeek())) {
                    continue;
                }

                boolean overlaps = current.getStartTime().isBefore(candidate.getEndTime())
                        && candidate.getStartTime().isBefore(current.getEndTime());
                if (overlaps) {
                    throw new IllegalStateException("Generated sub-sessions overlap for weekly session "
                            + weeklySessionPlan.getId());
                }
            }
        }
    }

    public EngineResult FFD_Alg(
            List<Goal> goals,
            List<AvailabilitySlot> availabilitySlots,
            WeeklySessionPlan plan,
            boolean usePriority) {

        // sorting section based on the user choise
        if (usePriority) {
            // sorting goals by priority then by duration
            goals.sort(Comparator.comparing((Goal g) -> g.getSubject().getPriority())
                    .thenComparing(Goal::getTargetHoursPerWeek, Comparator.reverseOrder()));
        } else {
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

        long currentGoalMinutes = (currentGoalIdx < goals.size())
                ? (long) (goals.get(currentGoalIdx).getTargetHoursPerWeek() * 60)
                : 0;
        long currentASlotMinutes = (currentASlotIdx < availabilitySlots.size()) ? Duration.between(
                availabilitySlots.get(currentASlotIdx).getStartTime(),
                availabilitySlots.get(currentASlotIdx).getEndTime()).toMinutes() : 0;

        while (currentGoalIdx < goals.size() && currentASlotIdx < availabilitySlots.size()) {
            Goal goal = goals.get(currentGoalIdx);
            AvailabilitySlot aSlot = availabilitySlots.get(currentASlotIdx);

            long fillAmount = Math.min(currentGoalMinutes, currentASlotMinutes);

            if (fillAmount > 0) {
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

            if (currentGoalMinutes <= 0) {
                currentGoalIdx++;
                if (currentGoalIdx < goals.size()) {
                    currentGoalMinutes = (long) (goals.get(currentGoalIdx).getTargetHoursPerWeek() * 60);
                }
            }

            if (currentASlotMinutes <= 0) {
                currentASlotIdx++;
                if (currentASlotIdx < availabilitySlots.size()) {
                    currentASlotMinutes = Duration.between(
                            availabilitySlots.get(currentASlotIdx).getStartTime(),
                            availabilitySlots.get(currentASlotIdx).getEndTime()).toMinutes();
                }
            }
        }

        while (currentGoalIdx < goals.size()) {
            totalPenaltyPoints += currentGoalMinutes;
            currentGoalIdx++;
            if (currentGoalIdx < goals.size()) {
                currentGoalMinutes = (long) (goals.get(currentGoalIdx).getTargetHoursPerWeek() * 60);
            }
        }

        return new EngineResult(
                generatedSubSessions,
                totalPenaltyPoints);
    }
}
