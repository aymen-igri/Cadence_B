package com.education.education.availability.availabilitySlot.mappers;

import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;
import com.education.education.availability.availabilitySlot.dto.response.CreateAvailabilitySlotRes;
import com.education.education.availability.availabilitySlot.entities.AvailabilitySlot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
public class AvailabilitySlotMapper {

    public AvailabilitySlot toAvailabilitySlot(CreateAvailabilitySlotReq request){
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setDayOfWeek(request.dayOfWeek());
        slot.setStartTime(request.start());
        slot.setEndTime(request.end());

        return slot;
    }

    public CreateAvailabilitySlotRes toCreateAvailabilitySlotRes(AvailabilitySlot slot){
        return new CreateAvailabilitySlotRes(
                slot.getId(),
                slot.getDayOfWeek(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCreatedAt()
        );
    }
}
