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

            return new GroupResponse(
                    group.getId(),
                    group.getName(),
                    group.getDescription(),
                    group.getPrivacyLevel(),
                    group.getMembers().size(),
                    group.getCreatedAt(),
                    membershipId,
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
    public GroupMemberResponse joinPublicGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (group.getPrivacyLevel() != GroupPrivacy.PUBLIC) {
            throw new AccessDeniedException("You can only directly join PUBLIC groups");
        }

        boolean alreadyMember = group.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));

        if (alreadyMember) {
            throw new IllegalArgumentException("You are already a member of this group");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GroupMember newMember = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .status(GroupMemberStatus.APPROVED)
                .build();

        GroupMember savedMember = groupMemberRepository.save(newMember);

        return new GroupMemberResponse(
                savedMember.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                savedMember.getRole(),
                savedMember.getStatus(),
                savedMember.getJoinedAt()
        );
    }
}
