package com.education.education.session.sharedSession.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.groups.entities.Group;
import com.education.education.session.sharedSession.enums.SharedSessionPermission;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.user.user.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shared_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedSession extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WeeklySessionPlan session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_user_id", nullable = false)
    private User sharedByUser;

    @Enumerated(EnumType.STRING)
    private SharedSessionPermission permission;

    @Builder.Default
    @Column(name = "shared_at", nullable = false, updatable = false)
    private LocalDateTime sharedAt = LocalDateTime.now();
}