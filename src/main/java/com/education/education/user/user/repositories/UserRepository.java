package com.education.education.user.user.repositories;

import com.education.education.user.user.entities.User;
import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
  User findByUsername(String username);

  User findByEmail(String email);

  User findByPhone(String phone);

  @Modifying
  @Query("UPDATE User u SET u.status = 'BANNED' " +
      "WHERE u.id = :id AND 'ROLE_GENERAL_USER' IN (SELECT r.role FROM u.role r )")
  void banUser(UUID id);

  @Modifying
  @Query("UPDATE User u SET u.status = 'ACTIVE' " +
      "WHERE u.id = :id AND 'ROLE_GENERAL_USER' IN (SELECT r.role FROM u.role r )")
  void unbanUser(UUID id);

  @Query("SELECT u FROM User u JOIN u.role r " +
      "WHERE r.role = 'ROLE_GENERAL_USER' " +
      "AND (:firstName IS NULL OR LOWER(u.firstName) LIKE :firstName) " +
      "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE :lastName) " +
      "AND (:email IS NULL OR LOWER(u.email) LIKE :email) " +
      "AND (:phone IS NULL OR u.phone LIKE :phone) " +
      "AND (:gender IS NULL OR u.gender = :gender) " +
      "AND (:status IS NULL OR u.status = :status)")
  Page<User> searchGeneralUser(
      String firstName,
      String lastName,
      String email,
      String phone,
      EGender gender,
      EStatus status,
      Pageable pageable);
}
