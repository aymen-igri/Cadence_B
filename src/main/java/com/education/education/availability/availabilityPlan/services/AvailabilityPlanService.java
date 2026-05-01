package com.education.education.availability.availabilityPlan.services;

import com.education.education.availability.availabilityPlan.dto.request.CreateAvailabilityPlanReq;
import com.education.education.availability.availabilityPlan.dto.response.CreateAvailabilityPlanRes;
import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.availability.availabilityPlan.mappers.AvailabilityPlanMapper;
import com.education.education.availability.availabilityPlan.repositories.AvailabilityPlanRepository;
import com.education.education.availability.availabilitySlot.dto.request.CreateAvailabilitySlotReq;
import com.education.education.availability.availabilitySlot.dto.response.CreateAvailabilitySlotRes;
import com.education.education.availability.availabilitySlot.mappers.AvailabilitySlotMapper;
import com.education.education.availability.availabilitySlot.repositories.AvailabilitySlotRepository;
import com.education.education.availability.availabilitySlot.services.AvailabilitySlotService;
import com.education.education.availability.dto.response.CreateAvailabilityRes;
import com.education.education.availability.dto.request.UpdateAvailabilitySlotsRequest;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AvailabilityPlanService {

    private final UserRepository userRepository;
    private final AvailabilityPlanRepository availabilityPlanRepository;
    private final AvailabilityPlanMapper availabilityPlanMapper;
    private final AvailabilitySlotMapper availabilitySlotMapper;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final AvailabilitySlotService availabilitySlotService;

    public CreateAvailabilityRes createAvailabilityPlan(
            UserDetails mainUser,
            CreateAvailabilityPlanReq planReq,
            List<CreateAvailabilitySlotReq> slotsReq) {
        AvailabilityPlan plan = availabilityPlanMapper.toAvailabiliytPlan(planReq);
        plan.setUser(userRepository.findByUsername(mainUser.getUsername()));
        AvailabilityPlan savedPlan = availabilityPlanRepository.save(plan);

        List<CreateAvailabilitySlotRes> createdSlots = new ArrayList<>();

        for (CreateAvailabilitySlotReq slotReq : slotsReq) {
            createdSlots.add(availabilitySlotService.createAvailabilitySlot(slotReq, savedPlan));
        }

        return new CreateAvailabilityRes(
                availabilityPlanMapper.toCreateAvailabilityPlanRes(savedPlan),
                createdSlots);
    }

    public List<CreateAvailabilityPlanRes> getAllAvailabilityPlans(
            UserDetails mainUser) {
        return availabilityPlanRepository.findAllByUser(
                userRepository.findByUsername(mainUser.getUsername()))
                .stream()
                .map(availabilityPlanMapper::toCreateAvailabilityPlanRes)
                .toList();
    }

    public CreateAvailabilityRes getAvailabilityPlan(
            UserDetails mainUser,
            UUID planId) {
        AvailabilityPlan plan = availabilityPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Availability Plan not found"));

        if (!plan.getUser().getUsername().equals(mainUser.getUsername())) {
            throw new RuntimeException("Unauthorized access to this availability plan");
        }

        List<CreateAvailabilitySlotRes> slots = plan.getSlots()
                .stream()
                .map(availabilitySlotMapper::toCreateAvailabilitySlotRes)
                .toList();

        return new CreateAvailabilityRes(
                availabilityPlanMapper.toCreateAvailabilityPlanRes(plan),
                slots);
    }

    public CreateAvailabilityRes updateAvailabilitySlots(
            UserDetails mainUser,
            UUID planId,
            UpdateAvailabilitySlotsRequest request) {
        AvailabilityPlan plan = availabilityPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Availability Plan not found"));

        if (!plan.getUser().getUsername().equals(mainUser.getUsername())) {
            throw new RuntimeException("Unauthorized access to this availability plan");
        }

        if (request.slots() == null || request.slots().isEmpty()) {
            throw new RuntimeException("Availability slots are required");
        }

        request.slots().forEach(slotReq -> {
            if (slotReq.start() == null || slotReq.end() == null) {
                throw new RuntimeException("Start time and end time are required");
            }

            if (!slotReq.start().isBefore(slotReq.end())) {
                throw new RuntimeException("Slot start time must be before end time");
            }
        });

        availabilitySlotRepository.deleteAllByAvailabilityPlan(plan);

        List<CreateAvailabilitySlotRes> updatedSlots = new ArrayList<>();
        for (CreateAvailabilitySlotReq slotReq : request.slots()) {
            updatedSlots.add(availabilitySlotService.createAvailabilitySlot(slotReq, plan));
        }

        return new CreateAvailabilityRes(
                availabilityPlanMapper.toCreateAvailabilityPlanRes(plan),
                updatedSlots);
    }
}
