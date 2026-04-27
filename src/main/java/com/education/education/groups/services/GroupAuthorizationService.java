package com.education.education.groups.services;

import com.education.education.groups.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupAuthorizationService {

    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public boolean isUserGroupMember(UUID groupId, UUID userId) {
        return groupRepository.findById(groupId)
                .map(group -> group.getMembers().stream()
                        .anyMatch(member -> member.getUser().getId().equals(userId)))
                .orElse(false);
    }
}