package com.education.education.session.sharedSession.services;

import com.education.education.groups.entities.Group;
import com.education.education.groups.enums.GroupRole;
import com.education.education.groups.repositories.GroupRepository;
import com.education.education.notification.services.NotificationService;
import com.education.education.exeption.WeeklySessionAlreadyExistsException;
import com.education.education.exeption.DuplicateSharedSessionException;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.sharedSession.DTO.ShareSessionRequest;
import com.education.education.session.sharedSession.DTO.SharedSessionRes;
import com.education.education.session.sharedSession.entities.SharedSession;
import com.education.education.session.sharedSession.enums.SharedSessionPermission;
import com.education.education.session.sharedSession.repositories.SharedSessionRepository;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.session.weeklySessionPlan.mappers.WeeklySessionPlanMapper;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SharedSessionService {

    private final SharedSessionRepository sharedSessionRepository;
    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final WeeklySessionPlanMapper weeklySessionPlanMapper;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SharedSessionRes shareSession(ShareSessionRequest request, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        WeeklySessionPlan session = weeklySessionPlanRepository.findById(request.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Weekly session plan not found"));

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        boolean isMember = group.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId()));

        if (!isMember) {
            throw new AccessDeniedException("You must be a member of the group to share to it");
        }

        if (sharedSessionRepository.existsBySessionIdAndGroupId(request.sessionId(), request.groupId())) {
            throw new DuplicateSharedSessionException("Session already shared with this group");
        }

        SharedSessionPermission permission = request.permission() != null ? request.permission()
                : SharedSessionPermission.VIEW_ONLY;

        SharedSession shared = SharedSession.builder()
                .session(session)
                .group(group)
                .sharedByUser(user)
                .permission(permission)
                .build();

        SharedSession saved = sharedSessionRepository.save(shared);

        String message = String.format("%s shared a session '%s' with your group %s", user.getUsername(),
                session.getTitle(), group.getName());
        for (var member : group.getMembers()) {
            notificationService.sendNotification(member.getUser().getId(), message, "Session Shared", "GROUP_UPDATE");
        }

        return toDto(saved);
    }

    public List<SharedSessionRes> getSharedSessionsForGroup(UUID groupId, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        boolean isMember = group.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId()));

        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return sharedSessionRepository.findByGroup_Id(groupId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CreateSessionRes forkSharedSession(UUID sharedSessionId, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        SharedSession sharedSession = sharedSessionRepository.findById(sharedSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Shared session not found"));

        Group group = sharedSession.getGroup();
        boolean isMember = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));

        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        WeeklySessionPlan sourceSession = sharedSession.getSession();

        if (weeklySessionPlanRepository.existsByUser_IdAndWeekYearAndWeekNumber(
                user.getId(), sourceSession.getWeekYear(), sourceSession.getWeekNumber())) {
            throw new WeeklySessionAlreadyExistsException(sourceSession.getWeekYear(), sourceSession.getWeekNumber());
        }

        WeeklySessionPlan forkedSession = new WeeklySessionPlan();
        forkedSession.setTitle(sourceSession.getTitle());
        forkedSession.setWeekYear(sourceSession.getWeekYear());
        forkedSession.setWeekNumber(sourceSession.getWeekNumber());
        forkedSession.setSessionStatus(ESessionStatus.UPCOMING);
        forkedSession.setGenerationType(sourceSession.getGenerationType());
        forkedSession.setGenerationAlgoType(sourceSession.getGenerationAlgoType());
        forkedSession.setPenaltyPoints(sourceSession.getPenaltyPoints());
        forkedSession.setAvailabilityPlan(sourceSession.getAvailabilityPlan());
        forkedSession.setPlanStatus(EPlanStatus.PUBLISHED);
        forkedSession.setUser(user);

        List<SubSession> forkedSubSessions = new ArrayList<>();
        if (sourceSession.getSubSessions() != null) {
            for (SubSession sourceSubSession : sourceSession.getSubSessions()) {
                SubSession forkedSubSession = new SubSession();
                forkedSubSession.setDayOfWeek(sourceSubSession.getDayOfWeek());
                forkedSubSession.setStartTime(sourceSubSession.getStartTime());
                forkedSubSession.setEndTime(sourceSubSession.getEndTime());
                forkedSubSession.setSubSessionStatus(
                        com.education.education.session.subSession.enums.ESubSessionStatus.PENDING);
                forkedSubSession.setSubject(sourceSubSession.getSubject());
                forkedSubSession.setGoal(sourceSubSession.getGoal());
                forkedSubSession.setWeeklySessionPlan(forkedSession);
                forkedSubSessions.add(forkedSubSession);
            }
        }

        forkedSession.setSubSessions(forkedSubSessions);
        WeeklySessionPlan savedFork = weeklySessionPlanRepository.save(forkedSession);

        return new CreateSessionRes(
                weeklySessionPlanMapper.toCreateWeeklySessionRes(savedFork),
                savedFork.getSubSessions().stream()
                        .map(this::toCreateSubSessionRes)
                        .toList());
    }

    public void unshareSession(UUID sessionId, UUID groupId, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        SharedSession shared = sharedSessionRepository.findBySession_IdAndGroup_Id(sessionId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Shared session not found"));

        WeeklySessionPlan session = shared.getSession();
        Group group = shared.getGroup();

        boolean isOwner = session.getUser().getId().equals(user.getId());
        boolean isSharedBy = shared.getSharedByUser().getId().equals(user.getId());
        boolean isGroupAdminOrOwner = group.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId())
                        && (m.getRole() == GroupRole.ADMIN || m.getRole() == GroupRole.OWNER));

        if (!(isOwner || isSharedBy || isGroupAdminOrOwner)) {
            throw new AccessDeniedException("You are not allowed to unshare this session");
        }

        sharedSessionRepository.delete(shared);

        String message = String.format("Session '%s' was unshared from your group %s", session.getTitle(),
                group.getName());
        for (var member : group.getMembers()) {
            notificationService.sendNotification(member.getUser().getId(), message, "Session Unshared", "GROUP_UPDATE");
        }
    }

    private SharedSessionRes toDto(SharedSession s) {
        return new SharedSessionRes(
                s.getId(),
                s.getSession().getId(),
                s.getSession().getTitle(),
                s.getGroup().getId(),
                s.getGroup().getName(),
                s.getSharedAt(),
                s.getSharedByUser().getId(),
                s.getSharedByUser().getUsername(),
                s.getPermission());
    }

    private CreateSubSessionRes toCreateSubSessionRes(SubSession subSession) {
        return new CreateSubSessionRes(
                subSession.getId(),
                subSession.getDayOfWeek(),
                subSession.getStartTime(),
                subSession.getEndTime(),
                subSession.getSubSessionStatus(),
                subSession.getSubject().getId(),
                subSession.getSubject().getName());
    }
}
