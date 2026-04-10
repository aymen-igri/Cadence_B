package com.education.education.availability.availabilityPlan.entities;

import com.education.education.availability.availabilityPlan.enums.EAvailabilityStatus;
import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.user.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "availability_plans")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AvailabilityPlan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "availability_plan_id")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EAvailabilityStatus availabilityStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
