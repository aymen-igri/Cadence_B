package com.education.education.session.weeklySessionPlan.services;

import com.education.education.session.dto.request.UpdateSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.request.UpdateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.education.education.session.subSession.dto.request.UpdateSubSessionStatusReq;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class WeeklySessionPlanService {

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
        session.setUser(userRepository.findByUsername(mainUser.getUsername()));
        WeeklySessionPlan savedSession = weeklySessionPlanRepository.save(session);

        List<CreateSubSessionRes> createdSubSessions = new ArrayList<>();

        for (CreateSubSessionReq subSessionReq : subSessionsReq) {
            createdSubSessions.add(subSessionPlanService.createSubSession(subSessionReq, savedSession));
        }

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

        if (weeklySessionPlan.getSessionStatus() != ESessionStatus.PENDING) {
            throw new IllegalStateException("Weekly session is not pending and cannot be modified");
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

        return subSessionMapper.toCreateSubSessionRes(saved);
    }

    public List<CreateSessionRes> getAllWeeklySessionPlans(UserDetails mainUser) {
        User user = userRepository.findByUsername(mainUser.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<WeeklySessionPlan> plans = weeklySessionPlanRepository.findByUserOrderByStartTimeDesc(user);

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
}
