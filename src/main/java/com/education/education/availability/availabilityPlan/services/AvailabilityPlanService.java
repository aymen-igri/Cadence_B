package com.education.education.availability.availabilityPlan.services;

import com.education.education.availability.availabilityPlan.dto.request.CreateAvailabilityPlanReq;
import com.education.education.availability.availabilityPlan.dto.response.CreateAvailabilityPlanRes;
import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilityPlan.mappers.AvailabilityPlanMapper;
import com.education.education.availability.availabilityPlan.repositories.AvailabilityPlanRepository;
import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;
import com.education.education.availability.availabilitySlot.dto.response.CreateAvailabilitySlotRes;
import com.education.education.availability.availabilitySlot.services.AvailabilitySlotService;
import com.education.education.availability.dto.response.CreateAvailabilityRes;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class AvailabilityPlanService {

    private final UserRepository userRepository;
    private final AvailabilityPlanRepository availabilityPlanRepository;
    private final AvailabilityPlanMapper availabilityPlanMapper;
    private final AvailabilitySlotService availabilitySlotService;

    public CreateAvailabilityRes createAvailabilityPlan(
            UserDetails mainUser,
            CreateAvailabilityPlanReq planReq,
            List<CreateAvailabilitySlotReq> slotsReq
    ){
        AvailabilityPlan plan = availabilityPlanMapper.toAvailabiliytPlan(planReq);
        plan.setUser(userRepository.findByUsername(mainUser.getUsername()));
        AvailabilityPlan savedPlan = availabilityPlanRepository.save(plan);

        List<CreateAvailabilitySlotRes> createdSlots = new ArrayList<>();

        for (CreateAvailabilitySlotReq slotReq:slotsReq){
            createdSlots.add(availabilitySlotService.createAvailabilitySlot(slotReq,savedPlan));
        }

        return new CreateAvailabilityRes(
                availabilityPlanMapper.toCreateAvailabilityPlanRes(savedPlan),
                createdSlots
        );
    }

    public List<CreateAvailabilityPlanRes> getAllAvailabilityPlans(
            UserDetails mainUser
    ) {
        return availabilityPlanRepository.findAllByUser(
                        userRepository.findByUsername(mainUser.getUsername())
                )
                .stream()
                .map(availabilityPlanMapper::toCreateAvailabilityPlanRes)
                .toList();
    }
}
