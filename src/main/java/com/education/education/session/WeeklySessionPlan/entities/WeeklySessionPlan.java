package com.education.education.session.WeeklySessionPlan.entities;

import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.session.WeeklySessionPlan.enums.ESessionStatus;
import com.education.education.user.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weekly_session")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeeklySessionPlan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "session_id")
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ESessionStatus sessionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}