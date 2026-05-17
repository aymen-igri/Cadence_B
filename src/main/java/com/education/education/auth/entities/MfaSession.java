package com.education.education.auth.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.education.education.auth.enums.EMfaType;
import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.user.user.entities.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MfaSession")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MfaSession extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private LocalDateTime expiry;

  @Builder.Default
  private boolean isUsed = false;

  @Column(nullable = true)
  @Builder.Default
  private int attempts = 0;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EMfaType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiry);
  }
}
