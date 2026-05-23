package com.education.education.groups.repositories;

import com.education.education.groups.entities.GroupJoinRequest;
import com.education.education.groups.enums.JoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, UUID> {
  List<GroupJoinRequest> findByGroupIdAndStatus(UUID groupId, JoinRequestStatus status);

  Optional<GroupJoinRequest> findByGroupIdAndUserId(UUID groupId, UUID userId);

  Integer countByGroupIdAndStatus(UUID groupId, JoinRequestStatus status);
}
