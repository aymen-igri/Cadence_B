package com.education.education.availability.availabilitySlot.repositories;

import com.education.education.availability.availabilitySlot.entities.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {
}
