package com.education.education.availability.availabilitySlot.entities;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.base.auditableEntity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "availability_slots")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AvailabilitySlot extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "availability_slot_id")
    private UUID id;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_plan_id")
    private AvailabilityPlan availabilityPlan;
}
