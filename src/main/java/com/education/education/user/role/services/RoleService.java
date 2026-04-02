package com.education.education.user.role.services;

import com.education.education.user.role.dto.request.AddRoleToUserRequest;
import com.education.education.user.role.dto.request.NewRoleRequest;
import com.education.education.user.role.dto.response.AddRoleToUserResponse;
import com.education.education.user.role.dto.response.NewRoleResponse;
import com.education.education.user.role.entities.Role;
import com.education.education.user.role.mappers.RoleMapper;
import com.education.education.user.role.repositories.RoleRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public NewRoleResponse createRole(NewRoleRequest request){
        Role role = roleMapper.toRole(request);
        roleRepository.save(role);
        return roleMapper.toNewRoleResponse(role);
    }

    public AddRoleToUserResponse addRoleToUser(AddRoleToUserRequest request){
        User user = userRepository.findByUsername(request.username());
        Role role = roleRepository.findByRole(request.roleName());
        
        user.getRole().add(role);
        userRepository.save(user);
        
        // Return your response logic here
        return roleMapper.toAddRoleUserResponse(user); // Adjust this based on your actual response constructor/mapper
    }
}
