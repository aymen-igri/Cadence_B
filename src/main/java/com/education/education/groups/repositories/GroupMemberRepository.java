package com.education.education.groups.repositories;

import com.education.education.groups.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
  List<GroupMember> findByGroupId(UUID groupId);
}
