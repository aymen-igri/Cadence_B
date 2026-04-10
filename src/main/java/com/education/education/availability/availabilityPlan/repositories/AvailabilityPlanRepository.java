package com.education.education.availability.availabilityPlan.repositories;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvailabilityPlanRepository extends JpaRepository<AvailabilityPlan, UUID>{
}
