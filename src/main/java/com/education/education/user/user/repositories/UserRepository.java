package com.education.education.user.user.repositories;

import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
  User findByUsername(String username);

  User findByEmail(String email);

  User findByPhone(String phone);
}
