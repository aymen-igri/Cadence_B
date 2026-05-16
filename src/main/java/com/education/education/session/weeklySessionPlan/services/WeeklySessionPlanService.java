package com.education.education.session.weeklySessionPlan.services;

import com.education.education.session.dto.request.UpdateSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.StruggleSubjectRes;
import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.request.UpdateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.dto.response.MissedSubSessionRes;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.mappers.SubSessionMapper;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.session.subSession.services.SubSessionService;
import com.education.education.session.weeklySessionPlan.dto.request.CreateWeeklySessionReq;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.mappers.WeeklySessionPlanMapper;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.subject.entities.Subject;
import com.education.education.subject.repositories.SubjectRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import com.education.education.exeption.PastWeekException;
import com.education.education.exeption.WeeklySessionAlreadyExistsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.education.education.session.subSession.dto.request.UpdateSubSessionStatusReq;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.temporal.WeekFields;

@Service
@Transactional
@AllArgsConstructor
public class WeeklySessionPlanService {

    private static final DateTimeFormatter STRUGGLE_WEEK_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy",
            Locale.ENGLISH);

    private final UserRepository userRepository;
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final WeeklySessionPlanMapper weeklySessionPlanMapper;
    private final SubSessionService subSessionPlanService;
    private final SubSessionRepository subSessionRepository;
    private final SubSessionMapper subSessionMapper;
    private final SubjectRepository subjectRepository;

    public CreateSessionRes createWeeklySessionPlan(
            UserDetails mainUser,
            CreateWeeklySessionReq sessionReq,
            List<CreateSubSessionReq> subSessionsReq) {
        WeeklySessionPlan session = weeklySessionPlanMapper.toWeeklySessionPlan(sessionReq);
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        session.setUser(user);
        validateWeekIsCurrentOrFuture(session.getWeekYear(), session.getWeekNumber());

        if (weeklySessionPlanRepository.existsByUser_IdAndWeekYearAndWeekNumber(
                user.getId(), session.getWeekYear(), session.getWeekNumber())) {
            throw new WeeklySessionAlreadyExistsException(session.getWeekYear(), session.getWeekNumber());
        }

        WeeklySessionPlan savedSession = weeklySessionPlanRepository.save(session);

        List<CreateSubSessionRes> createdSubSessions = new ArrayList<>();

        for (CreateSubSessionReq subSessionReq : subSessionsReq) {
            createdSubSessions.add(subSessionPlanService.createSubSession(subSessionReq, savedSession));
        }

        deriveStatus(savedSession);

        return new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(savedSession),
                createdSubSessions);
    }

    public CreateSubSessionRes updateSubSessionStatus(
            UUID weeklySessionId,
            UUID subSessionId,
            UpdateSubSessionStatusReq request,
            UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(weeklySessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to modify this weekly session plan");
        }

        if (weeklySessionPlan.getSessionStatus() != ESessionStatus.UPCOMING) {
            throw new IllegalStateException("Weekly session is not upcoming and cannot be modified");
        }

        SubSession subSession = subSessionRepository.findById(subSessionId)
                .orElseThrow(() -> new IllegalArgumentException("SubSession not found"));

        if (subSession.getWeeklySessionPlan() == null
                || !subSession.getWeeklySessionPlan().getId().equals(weeklySessionId)) {
            throw new IllegalArgumentException("SubSession does not belong to the provided weekly session");
        }

        ESubSessionStatus newStatus = request.status();
        subSession.setSubSessionStatus(newStatus);
        SubSession saved = subSessionRepository.save(subSession);

        deriveStatus(weeklySessionPlan);

        return subSessionMapper.toCreateSubSessionRes(saved);
    }

    public List<CreateSessionRes> getAllWeeklySessionPlans(UserDetails mainUser, EPlanStatus planStatus) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<WeeklySessionPlan> plans = weeklySessionPlanRepository.findByUserAndPlanStatusOrderByStartTimeDesc(user,
                planStatus);

        return plans.stream().map(plan -> new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(plan),
                subSessionPlanService.getSubSessionsByPlan(plan))).toList();
    }

    public void deleteWeeklySessionPlan(UUID sessionId, UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this weekly session plan");
        }

        weeklySessionPlanRepository.delete(weeklySessionPlan);
    }

    public CreateSessionRes approveSession(UUID sessionId, UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this weekly session plan");
        }

        weeklySessionPlan.setPlanStatus(EPlanStatus.PUBLISHED);
        WeeklySessionPlan saved = weeklySessionPlanRepository.save(weeklySessionPlan);

        return new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(saved),
                subSessionPlanService.getSubSessionsByPlan(saved));
    }

    public CreateSessionRes getWeeklySessionWeekly(UUID sessionId, UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this weekly session plan");
        }

        return new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(weeklySessionPlan),
                subSessionPlanService.getSubSessionsByPlan(weeklySessionPlan));
    }

    public List<MissedSubSessionRes> getMissedSubSessions(UUID sessionId, UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to view this weekly session plan");
        }

        return subSessionRepository.findByWeeklySessionPlanOrderByStartTimeAsc(weeklySessionPlan).stream()
                .filter(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.INCOMPLETED
                        || subSession.getSubSessionStatus() == ESubSessionStatus.CLOSED)
                .map(subSession -> new MissedSubSessionRes(
                        subSession.getId(),
                        subSession.getSubject().getId(),
                        subSession.getSubject().getName(),
                        subSession.getDayOfWeek(),
                        subSession.getStartTime(),
                        subSession.getEndTime()))
                .toList();
    }

    public List<StruggleSubjectRes> getStruggleDetection(UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<WeeklySessionPlan> recentCompletedOrClosedPlans = weeklySessionPlanRepository
                .findByUserOrderByStartTimeDesc(user)
                .stream()
                .filter(plan -> plan.getSessionStatus() == ESessionStatus.COMPLETED
                        || plan.getSessionStatus() == ESessionStatus.CLOSED)
                .limit(4)
                .toList();

        if (recentCompletedOrClosedPlans.isEmpty()) {
            return List.of();
        }

        List<Subject> subjects = subjectRepository.findByCreatedBy(user);
        if (subjects.isEmpty()) {
            return List.of();
        }

        Map<UUID, Integer> missedWeeksBySubjectId = new HashMap<>();
        Map<UUID, String> lastCompletedWeekBySubjectId = new HashMap<>();
        Set<UUID> subjectIds = subjects.stream()
                .map(Subject::getId)
                .collect(Collectors.toSet());

        for (WeeklySessionPlan plan : recentCompletedOrClosedPlans) {
            String weekLabel = buildWeekLabel(plan);
            Set<UUID> completedSubjectIds = subSessionRepository.findByWeeklySessionPlanOrderByStartTimeAsc(plan)
                    .stream()
                    .filter(subSession -> subSession.getSubject() != null)
                    .filter(subSession -> subjectIds.contains(subSession.getSubject().getId()))
                    .filter(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.COMPLETED)
                    .map(subSession -> subSession.getSubject().getId())
                    .collect(Collectors.toSet());

            for (Subject subject : subjects) {
                UUID subjectId = subject.getId();
                if (completedSubjectIds.contains(subjectId)) {
                    lastCompletedWeekBySubjectId.putIfAbsent(subjectId, weekLabel);
                } else {
                    missedWeeksBySubjectId.merge(subjectId, 1, Integer::sum);
                }
            }
        }

        return subjects.stream()
                .map(subject -> new StruggleSubjectRes(
                        subject.getId().toString(),
                        subject.getName(),
                        missedWeeksBySubjectId.getOrDefault(subject.getId(), 0),
                        lastCompletedWeekBySubjectId.get(subject.getId())))
                .filter(res -> res.missedWeeksCount() >= 3)
                .sorted(Comparator.comparingInt(StruggleSubjectRes::missedWeeksCount).reversed()
                        .thenComparing(StruggleSubjectRes::subjectName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public CreateSessionRes updateWeeklySessionPlan(
            UUID sessionId,
            UpdateSessionReq request,
            UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        WeeklySessionPlan weeklySessionPlan = weeklySessionPlanRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        if (!weeklySessionPlan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to update this weekly session plan");
        }

        if (request.weeklySession() != null) {
            weeklySessionPlanMapper.updateWeeklySessionFromReq(request.weeklySession(), weeklySessionPlan);
        }

        if (request.subSessions() != null) {
            reconcileSubSessions(weeklySessionPlan, user, request.subSessions());
        }

        deriveStatus(weeklySessionPlan);
        WeeklySessionPlan savedWeeklySessionPlan = weeklySessionPlanRepository.save(weeklySessionPlan);

        return new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(savedWeeklySessionPlan),
                subSessionPlanService.getSubSessionsByPlan(savedWeeklySessionPlan));
    }

    private void reconcileSubSessions(
            WeeklySessionPlan weeklySessionPlan,
            User user,
            List<UpdateSubSessionReq> subSessionRequests) {
        if (subSessionRequests.isEmpty()) {
            throw new IllegalArgumentException("At least one sub-session must remain");
        }

        List<SubSession> existingSubSessions = subSessionRepository
                .findByWeeklySessionPlanOrderByStartTimeAsc(weeklySessionPlan);
        Map<UUID, SubSession> subSessionById = new HashMap<>();
        for (SubSession subSession : existingSubSessions) {
            subSessionById.put(subSession.getId(), subSession);
        }

        Set<UUID> updatedSubSessionIds = new HashSet<>();
        int createdSubSessionCount = 0;

        for (UpdateSubSessionReq request : subSessionRequests) {
            if (request.id() == null) {
                createSubSessionFromUpdateRequest(weeklySessionPlan, user, request);
                createdSubSessionCount++;
                continue;
            }

            SubSession existingSubSession = subSessionById.get(request.id());
            if (existingSubSession == null) {
                throw new IllegalArgumentException("Sub-session not found for update");
            }

            validateSubSessionTimeForUpdate(existingSubSession, request);
            if (request.subjectId() != null) {
                validateSubjectOwnershipIfProvided(request.subjectId(), user);
            }

            subSessionMapper.updateSubSessionFromReq(request, existingSubSession);
            subSessionRepository.save(existingSubSession);
            updatedSubSessionIds.add(existingSubSession.getId());
        }

        long deletedSubSessionCount = existingSubSessions.stream()
                .filter(existingSubSession -> !updatedSubSessionIds.contains(existingSubSession.getId()))
                .count();

        long remainingSubSessionCount = existingSubSessions.size() - deletedSubSessionCount + createdSubSessionCount;
        if (remainingSubSessionCount < 1) {
            throw new IllegalArgumentException("At least one sub-session must remain");
        }

        for (SubSession existingSubSession : existingSubSessions) {
            if (!updatedSubSessionIds.contains(existingSubSession.getId())) {
                subSessionRepository.delete(existingSubSession);
            }
        }

        deriveStatus(weeklySessionPlan);
    }

    private void createSubSessionFromUpdateRequest(
            WeeklySessionPlan weeklySessionPlan,
            User user,
            UpdateSubSessionReq request) {
        if (request.dayOfWeek() == null || request.startTime() == null || request.endTime() == null
                || request.status() == null || request.subjectId() == null) {
            throw new IllegalArgumentException("All sub-session fields are required when creating a new sub-session");
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalArgumentException("Sub-session start time must be before end time");
        }

        validateSubjectOwnershipIfProvided(request.subjectId(), user);

        SubSession newSubSession = subSessionMapper.toSubSession(request);
        newSubSession.setWeeklySessionPlan(weeklySessionPlan);
        subSessionRepository.save(newSubSession);
    }

    private void validateSubSessionTimeForUpdate(SubSession existingSubSession, UpdateSubSessionReq request) {
        java.time.LocalTime nextStart = request.startTime() != null ? request.startTime()
                : existingSubSession.getStartTime();
        java.time.LocalTime nextEnd = request.endTime() != null ? request.endTime() : existingSubSession.getEndTime();

        if (!nextStart.isBefore(nextEnd)) {
            throw new IllegalArgumentException("Sub-session start time must be before end time");
        }
    }

    private void validateSubjectOwnershipIfProvided(UUID subjectId, User user) {
        if (subjectId == null) {
            return;
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        if (!subject.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to use this subject");
        }
    }

    private void validateWeekIsCurrentOrFuture(Integer weekYear, Integer weekNumber) {
        if (weekYear == null || weekNumber == null) {
            throw new IllegalArgumentException("Week year and week number are required");
        }

        WeekFields weekFields = WeekFields.ISO;
        LocalDate now = LocalDate.now();
        int currentWeekYear = now.get(weekFields.weekBasedYear());
        int currentWeekNumber = now.get(weekFields.weekOfWeekBasedYear());

        boolean isPastWeek = weekYear < currentWeekYear
                || (weekYear.equals(currentWeekYear) && weekNumber < currentWeekNumber);

        if (isPastWeek) {
            throw new PastWeekException(weekYear, weekNumber);
        }
    }

    public ESessionStatus deriveStatus(WeeklySessionPlan session) {
        ESessionStatus status = computeDerivedStatus(session);
        session.setSessionStatus(status);
        weeklySessionPlanRepository.save(session);
        return status;
    }

    private ESessionStatus computeDerivedStatus(WeeklySessionPlan session) {
        List<SubSession> subSessions = subSessionRepository.findByWeeklySessionPlanOrderByStartTimeAsc(session);
        if (subSessions.isEmpty()) {
            return ESessionStatus.UPCOMING;
        }

        LocalDate today = LocalDate.now();
        boolean weekHasPassed = today.isAfter(getWeekSunday(session.getWeekYear(), session.getWeekNumber()));

        boolean allCompleted = subSessions.stream()
                .allMatch(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.COMPLETED);
        if (allCompleted) {
            return ESessionStatus.COMPLETED;
        }

        if (weekHasPassed) {
            return ESessionStatus.CLOSED;
        }

        boolean allPending = subSessions.stream()
                .allMatch(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.PENDING);
        if (allPending) {
            return ESessionStatus.UPCOMING;
        }

        boolean anyActive = isAnySubSessionActiveNow(session, subSessions);
        if (anyActive) {
            return ESessionStatus.ACTIVE;
        }

        boolean hasCompleted = subSessions.stream()
                .anyMatch(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.COMPLETED);
        boolean hasIncompleted = subSessions.stream()
                .anyMatch(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.INCOMPLETED);

        if (hasCompleted && hasIncompleted) {
            return ESessionStatus.INCOMPLETED;
        }

        return ESessionStatus.UPCOMING;
    }

    private LocalDate getWeekSunday(Integer weekYear, Integer weekNumber) {
        WeekFields weekFields = WeekFields.ISO;
        return LocalDate.of(weekYear, 1, 4)
                .with(weekFields.weekOfWeekBasedYear(), weekNumber)
                .with(weekFields.dayOfWeek(), 7);
    }

    private boolean isAnySubSessionActiveNow(WeeklySessionPlan session, List<SubSession> subSessions) {
        WeekFields weekFields = WeekFields.ISO;
        LocalDateTime now = LocalDateTime.now();
        int currentWeekYear = now.get(weekFields.weekBasedYear());
        int currentWeekNumber = now.get(weekFields.weekOfWeekBasedYear());

        if (!session.getWeekYear().equals(currentWeekYear) || !session.getWeekNumber().equals(currentWeekNumber)) {
            return false;
        }

        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        return subSessions.stream()
                .filter(subSession -> subSession.getSubSessionStatus() == ESubSessionStatus.PENDING)
                .anyMatch(subSession -> subSession.getDayOfWeek() == currentDate.getDayOfWeek()
                        && !currentTime.isBefore(subSession.getStartTime())
                        && currentTime.isBefore(subSession.getEndTime()));
    }

    private String buildWeekLabel(WeeklySessionPlan plan) {
        LocalDate weekStart = plan.getStartTime() == null ? null : plan.getStartTime().toLocalDate();
        if (weekStart == null) {
            return null;
        }

        return "Week of " + weekStart.format(STRUGGLE_WEEK_LABEL_FORMATTER);
    }

}
