package com.education.education.availability.availabilitySlot.services;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;
import com.education.education.availability.availabilitySlot.dto.response.CreateAvailabilitySlotRes;
import com.education.education.availability.availabilitySlot.entities.AvailabilitySlot;
import com.education.education.availability.availabilitySlot.mappers.AvailabilitySlotMapper;
import com.education.education.availability.availabilitySlot.repositories.AvailabilitySlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final AvailabilitySlotMapper availabilitySlotMapper;

    public CreateAvailabilitySlotRes createAvailabilitySlot(
            CreateAvailabilitySlotReq slotReq,
            AvailabilityPlan savedPlan
    ) {
        AvailabilitySlot slot = availabilitySlotMapper.toAvailabilitySlot(slotReq);
        slot.setAvailabilityPlan(savedPlan);

        return availabilitySlotMapper.toCreateAvailabilitySlotRes(availabilitySlotRepository.save(slot));
    }
}
