package com.education.education.availability.availabilitySlot.repositories;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilitySlot.entities.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {

    List<AvailabilitySlot> findAllByAvailabilityPlan(AvailabilityPlan availabilityPlan);

    void deleteAllByAvailabilityPlan(AvailabilityPlan availabilityPlan);
}
