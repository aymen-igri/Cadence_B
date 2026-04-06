package com.education.education.groups.services;

import com.education.education.groups.DTO.request.CreateGroupRequest;
import com.education.education.groups.DTO.response.GroupResponse;
import com.education.education.groups.entities.Group;
import com.education.education.groups.entities.GroupMember;
import com.education.education.groups.enums.GroupMemberStatus;
import com.education.education.groups.enums.GroupRole;
import com.education.education.groups.repositories.GroupRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

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
                savedGroup.getMembers().get(0).getId().toString()
        );
    }
}
