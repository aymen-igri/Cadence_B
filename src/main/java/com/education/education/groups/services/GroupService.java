package com.education.education.groups.services;

import com.education.education.groups.DTO.request.CreateGroupRequest;
import com.education.education.groups.DTO.response.GroupMemberResponse;
import com.education.education.groups.DTO.response.GroupResponse;
import com.education.education.groups.entities.Group;
import com.education.education.groups.entities.GroupMember;
import com.education.education.groups.enums.GroupMemberStatus;
import com.education.education.groups.enums.GroupRole;
import com.education.education.groups.enums.GroupPrivacy;
import com.education.education.groups.repositories.GroupMemberRepository;
import com.education.education.groups.repositories.GroupRepository;
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

        verifyAdminOrOwner(group, requesterId);

        return group.getMembers().stream()
                .filter(member -> member.getStatus() == GroupMemberStatus.PENDING)
                .map(member -> createGroupMemberResponse(member, member.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveJoinRequest(UUID groupId, UUID targetUserId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        verifyAdminOrOwner(group, requesterId);

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Join request not found"));

        if (targetMember.getStatus() != GroupMemberStatus.PENDING) {
            throw new IllegalArgumentException("User is not in PENDING status");
        }

        targetMember.setStatus(GroupMemberStatus.APPROVED);
        groupMemberRepository.save(targetMember);
    }

    @Transactional
    public void rejectJoinRequest(UUID groupId, UUID targetUserId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        verifyAdminOrOwner(group, requesterId);

        GroupMember targetMember = group.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Join request not found"));

        if (targetMember.getStatus() != GroupMemberStatus.PENDING) {
            throw new IllegalArgumentException("User is not in PENDING status");
        }

        groupMemberRepository.delete(targetMember);
    }

    private void verifyAdminOrOwner(Group group, UUID userId) {
        boolean isAuthorized = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId) &&
                        member.getStatus() == GroupMemberStatus.APPROVED &&
                        (member.getRole() == GroupRole.OWNER || member.getRole() == GroupRole.ADMIN));

        if (!isAuthorized) {
            throw new AccessDeniedException("You do not have permission to perform this action");
        }
    }
}
