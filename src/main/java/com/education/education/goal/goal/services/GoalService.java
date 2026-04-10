package com.education.education.goal.goal.services;

import com.education.education.goal.dto.response.CreateGoalResponse;
import com.education.education.goal.goal.dto.request.CreateGoalReq;
import com.education.education.goal.goal.entities.Goal;
import com.education.education.goal.goal.mappers.GoalMapper;
import com.education.education.goal.goal.repositories.GoalRepository;
import com.education.education.goal.task.dto.request.CreateTaskReq;
import com.education.education.goal.task.dto.response.CreateTaskRes;
import com.education.education.goal.task.services.TaskService;
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
public class GoalService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final TaskService taskService;

    public CreateGoalResponse createGoal(
            UserDetails mainUser,
            CreateGoalReq goal,
            List<CreateTaskReq> tasks
    ){
        Goal newGoal= goalMapper.ToGoal(goal);
        newGoal.setUser(userRepository.findByUsername(mainUser.getUsername()));
        Goal savedGoal = goalRepository.save(newGoal);

        List<CreateTaskRes> createdTasks= new ArrayList<>();

        for(CreateTaskReq task:tasks){
            createdTasks.add(taskService.createTask(task, savedGoal));
        }

        return new CreateGoalResponse(
                goalMapper.toCreateGoalRes(savedGoal),
                createdTasks
        );
    }
}
