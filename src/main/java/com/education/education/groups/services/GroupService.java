package com.education.education.groups.services;

import com.education.education.groups.DTO.request.CreateGroupRequest;
import com.education.education.groups.DTO.request.UpdateGroupRequest;
import com.education.education.groups.DTO.response.GroupMemberResponse;
import com.education.education.groups.DTO.response.GroupResponse;
import com.education.education.groups.entities.Group;
import com.education.education.groups.entities.GroupMember;
import com.education.education.groups.enums.GroupMemberStatus;
import com.education.education.groups.enums.GroupRole;
import com.education.education.groups.enums.GroupPrivacy;
import com.education.education.groups.repositories.GroupMemberRepository;
import com.education.education.groups.repositories.GroupRepository;
import com.education.education.notification.services.NotificationService;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final NotificationService notificationService;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, UUID creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Group group = Group.builder()
                .name(request.name())
                .description(request.description())
                .privacyLevel(request.privacyLevel())
                .members(new ArrayList<>())
                .build();

        GroupMember ownerMember = GroupMember.builder()
                .group(group)
                .user(creator)
                .role(GroupRole.OWNER)
                .status(GroupMemberStatus.APPROVED)
                .build();

        group.getMembers().add(ownerMember);

        Group savedGroup = groupRepository.save(group);

        return new GroupResponse(
                savedGroup.getId(),
                savedGroup.getName(),
                savedGroup.getDescription(),
                savedGroup.getPrivacyLevel(),
                savedGroup.getMembers().size(),
                savedGroup.getCreatedAt(),
                savedGroup.getMembers().get(0).getId().toString(),
                GroupMemberStatus.APPROVED,
                savedGroup.getMembers().get(0).getRole()
        );
    }

    public List<GroupResponse> getAllGroups(UUID currentUserId) {
        List<Group> allGroups = groupRepository.findAll();
        
        return allGroups.stream().map(group -> {
            Optional<GroupMember> userMembership = group.getMembers().stream()
                    .filter(member -> member.getUser().getId().equals(currentUserId))
                    .findFirst();

            String membershipId = userMembership.map(member -> member.getId().toString()).orElse(null);
            GroupRole userRole = userMembership.map(GroupMember::getRole).orElse(null);
            GroupMemberStatus membershipStatus = userMembership.map(GroupMember::getStatus).orElse(null);

            return new GroupResponse(
                    group.getId(),
                    group.getName(),
                    group.getDescription(),
                    group.getPrivacyLevel(),
                    group.getMembers().size(),
                    group.getCreatedAt(),
                    membershipId,
                    membershipStatus,
                    userRole
            );
        }).collect(Collectors.toList());
    }

    public List<GroupMemberResponse> getGroupMembers(UUID groupId, UUID currentUserId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        boolean isMember = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(currentUserId));

        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return group.getMembers().stream()
                .map(member -> new GroupMemberResponse(
                        member.getId(),
                        member.getUser().getId(),
                        member.getUser().getFirstName(),
                        member.getUser().getLastName(),
                        member.getUser().getUsername(),
                        member.getRole(),
                        member.getStatus(),
                        member.getJoinedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupMemberResponse joinGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        Optional<GroupMember> existingMemberOpt = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(userId))
                .findFirst();

        if (existingMemberOpt.isPresent()) {
            GroupMember existingMember = existingMemberOpt.get();
            if (existingMember.getStatus() == GroupMemberStatus.PENDING) {
                throw new IllegalArgumentException("You already have a pending join request for this group");
            } else if (existingMember.getStatus() == GroupMemberStatus.INVITED) {
                // If invited, joining automatically approves the invitation
                existingMember.setStatus(GroupMemberStatus.APPROVED);
                GroupMember savedMember = groupMemberRepository.save(existingMember);
                return createGroupMemberResponse(savedMember, savedMember.getUser());
            } else {
                throw new IllegalArgumentException("You are already a member of this group");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GroupMemberStatus initialStatus = (group.getPrivacyLevel() == GroupPrivacy.PUBLIC) 
                ? GroupMemberStatus.APPROVED 
                : GroupMemberStatus.PENDING;

        GroupMember newMember = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .status(initialStatus)
                .build();

        GroupMember savedMember = groupMemberRepository.save(newMember);

        return createGroupMemberResponse(savedMember, user);
    }

    private GroupMemberResponse createGroupMemberResponse(GroupMember member, User user) {
        return new GroupMemberResponse(
                member.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                member.getRole(),
                member.getStatus(),
                member.getJoinedAt()
        );
    }

    public List<GroupMemberResponse> getPendingRequests(UUID groupId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!verifyAdminOrOwner(group, requesterId)) {
            return new ArrayList<>();
        }

        return group.getMembers().stream()
                .filter(member -> member.getStatus() == GroupMemberStatus.PENDING)
                .map(member -> createGroupMemberResponse(member, member.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveJoinRequest(UUID groupId, UUID targetUserId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!verifyAdminOrOwner(group, requesterId)) {
            throw new AccessDeniedException("You do not have permission to approve join requests");
        }

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Join request not found"));

        if (targetMember.getStatus() != GroupMemberStatus.PENDING) {
            throw new IllegalArgumentException("User is not in PENDING status");
        }

        targetMember.setStatus(GroupMemberStatus.APPROVED);
        groupMemberRepository.save(targetMember);
        notificationService.sendNotification(
                targetMember.getUser().getId(),
                "Your join request to the group: " + group.getName() + " has been approved.",
                "Join Request Approved",
                "GROUP_UPDATE"
        );
    }

    @Transactional
    public void rejectJoinRequest(UUID groupId, UUID targetUserId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!verifyAdminOrOwner(group, requesterId)) {
            throw new AccessDeniedException("You do not have permission to reject join requests");
        }

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Join request not found"));

        if (targetMember.getStatus() != GroupMemberStatus.PENDING) {
            throw new IllegalArgumentException("User is not in PENDING status");
        }

        groupMemberRepository.delete(targetMember);

        notificationService.sendNotification(
                targetMember.getUser().getId(),
                "Your join request to the group: " + group.getName() + " has been rejected.",
                "Join Request Rejected",
                "GROUP_UPDATE"
        );
    }

    private boolean verifyAdminOrOwner(Group group, UUID userId) {
        boolean isAuthorized = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId) &&
                        member.getStatus() == GroupMemberStatus.APPROVED &&
                        (member.getRole() == GroupRole.OWNER || member.getRole() == GroupRole.ADMIN));

        return isAuthorized;
    }

    @Transactional
    public GroupResponse updateGroup(UUID groupId, UpdateGroupRequest request, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!verifyAdminOrOwner(group, requesterId)) {
            throw new AccessDeniedException("You do not have permission to update this group");
        }

        if (request.name() != null && !request.name().trim().isEmpty()) {
            group.setName(request.name());
        }
        if (request.description() != null) {
            group.setDescription(request.description());
        }
        if (request.privacyLevel() != null) {
            group.setPrivacyLevel(request.privacyLevel());
        }

        Group savedGroup = groupRepository.save(group);

        Optional<GroupMember> userMembership = savedGroup.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(requesterId))
                .findFirst();

        String membershipId = userMembership.map(member -> member.getId().toString()).orElse(null);
        GroupRole userRole = userMembership.map(GroupMember::getRole).orElse(null);
        GroupMemberStatus membershipStatus = userMembership.map(GroupMember::getStatus).orElse(null);

        return new GroupResponse(
                savedGroup.getId(),
                savedGroup.getName(),
                savedGroup.getDescription(),
                savedGroup.getPrivacyLevel(),
                savedGroup.getMembers().size(),
                savedGroup.getCreatedAt(),
                membershipId,
                membershipStatus,
                userRole
        );
    }

    @Transactional
    public void deleteGroup(UUID groupId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        boolean isOwner = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(requesterId) &&
                        member.getStatus() == GroupMemberStatus.APPROVED &&
                        member.getRole() == GroupRole.OWNER);

        if (!isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this group. Only the owner can delete the group.");
        }

        groupRepository.delete(group);
    }

    @Transactional
    public void transferOwnership(UUID groupId, UUID targetUserId, UUID requesterId) {
        if (requesterId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot transfer ownership to yourself");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        GroupMember currentOwner = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(requesterId) &&
                        member.getStatus() == GroupMemberStatus.APPROVED &&
                        member.getRole() == GroupRole.OWNER)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You do not have permission to transfer ownership. Only the owner can do this."));

        GroupMember newOwner = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target user is not a member of the group"));

        if (newOwner.getStatus() != GroupMemberStatus.APPROVED) {
            throw new IllegalArgumentException("Target user must be an approved member to become owner");
        }

        currentOwner.setRole(GroupRole.MEMBER);
        newOwner.setRole(GroupRole.OWNER);

        groupMemberRepository.save(currentOwner);
        groupMemberRepository.save(newOwner);

        notificationService.sendNotification(
                newOwner.getUser().getId(),
                "You have been transferred ownership of the group: " + group.getName(),
                "Group Ownership Transferred",
                "GROUP_ROLE_UPDATE"
        );
    }

    @Transactional
    public void leaveGroup(UUID groupId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        GroupMember currentMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(requesterId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this group"));

        if (currentMember.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("Owner cannot leave the group. Transfer ownership or delete the group first.");
        }
        group.getMembers().remove(currentMember);
        groupMemberRepository.delete(currentMember);
    }

    @Transactional
    public void removeMember(UUID groupId, UUID targetUserMemberId, UUID requesterId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        GroupMember requesterMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(requesterId) && member.getStatus() == GroupMemberStatus.APPROVED)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not an approved member of this group"));

        if (requesterMember.getId().equals(targetUserMemberId)) {
            throw new IllegalArgumentException("You cannot remove yourself. Use the leave group functionality instead.");
        }

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getId().equals(targetUserMemberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target user is not a member of the group"));

        if (requesterMember.getRole() == GroupRole.MEMBER) {
            throw new AccessDeniedException("Members cannot remove other users");
        }

        if (requesterMember.getRole() == GroupRole.ADMIN && targetMember.getRole() != GroupRole.MEMBER) {
            throw new AccessDeniedException("Admins can only remove regular members");
        }

        if (requesterMember.getRole() == GroupRole.OWNER && targetMember.getRole() == GroupRole.OWNER) {
            throw new AccessDeniedException("Owners cannot remove other owners"); // Typically there's only one owner, but safe to check
        }

        group.getMembers().remove(targetMember); 
        groupMemberRepository.delete(targetMember);

        notificationService.sendNotification(
                targetMember.getUser().getId(),
                "You have been removed from the group: " + group.getName(),
                "Removed from Group",
                "GROUP_UPDATE"
        );
    }

    @Transactional
    public void promoteMember(UUID groupId, UUID targetUserMemberShipId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        GroupMember requesterMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(requesterId) && member.getStatus() == GroupMemberStatus.APPROVED)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not an approved member of this group"));

        if (requesterMember.getRole() == GroupRole.MEMBER) {
            throw new AccessDeniedException("Only owners and admins can promote members");
        }

        if (requesterMember.getId().equals(targetUserMemberShipId)) {
            throw new IllegalArgumentException("You cannot promote yourself.");
        }

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getId().equals(targetUserMemberShipId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target user is not a member of the group"));

        if (targetMember.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("Target user is already the owner");
        }
        
        if (targetMember.getRole() == GroupRole.ADMIN) {
            throw new IllegalArgumentException("Target user is already an admin");
        }

        targetMember.setRole(GroupRole.ADMIN);
        groupMemberRepository.save(targetMember);

        notificationService.sendNotification(
                targetMember.getUser().getId(),
                "You have been promoted to ADMIN in the group: " + group.getName(),
                "Promoted to Admin",
                "GROUP_ROLE_UPDATE"
        );
    }

    @Transactional
    public void demoteAdmin(UUID groupId, UUID targetUserMemberShipId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        GroupMember requesterMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(requesterId) && member.getStatus() == GroupMemberStatus.APPROVED)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not an approved member of this group"));

        if (requesterMember.getRole() != GroupRole.OWNER) {
            throw new AccessDeniedException("Only the owner can demote an admin");
        }

        if (requesterMember.getId().equals(targetUserMemberShipId)) {
            throw new IllegalArgumentException("You cannot demote yourself.");
        }

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getId().equals(targetUserMemberShipId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target user is not a member of the group"));

        if (targetMember.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("Cannot demote an owner");
        }

        if (targetMember.getRole() == GroupRole.MEMBER) {
            throw new IllegalArgumentException("Target user is already a member");
        }

        targetMember.setRole(GroupRole.MEMBER);
        groupMemberRepository.save(targetMember);

        notificationService.sendNotification(
                targetMember.getUser().getId(),
                "You have been demoted to MEMBER in the group: " + group.getName(),
                "Demoted to Member",
                "GROUP_ROLE_UPDATE"
        );
    }
}
