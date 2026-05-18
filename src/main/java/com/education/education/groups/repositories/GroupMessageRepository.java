package com.education.education.groups.repositories;

import com.education.education.groups.entities.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, UUID> {
    List<GroupMessage> findByGroupIdOrderBySentAtAsc(UUID groupId);

    Page<GroupMessage> findByGroupId(UUID groupId, Pageable pageable);
}
