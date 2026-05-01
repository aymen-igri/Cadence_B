package com.education.education.availability.availabilityPlan.repositories;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvailabilityPlanRepository extends JpaRepository<AvailabilityPlan, UUID>{

	List<AvailabilityPlan> findAllByUser(User user);
}
