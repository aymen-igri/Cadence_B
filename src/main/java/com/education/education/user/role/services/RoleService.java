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

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Transactional
public class RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public NewRoleResponse createRole(NewRoleRequest request){
        Role existingRole = roleRepository.findByRole(request.role());
        if (existingRole != null){
            throw new IllegalArgumentException("Role already exists");
        }
        Role role = roleMapper.toRole(request);
        roleRepository.save(role);
        return roleMapper.toNewRoleResponse(role);
    }

    public AddRoleToUserResponse addRoleToUser(AddRoleToUserRequest request){
        User user = userRepository.findByUsername(request.username());
        Role role = roleRepository.findByRole(request.roleName());

        if (role == null){
            throw new IllegalArgumentException("Role not found");
        }

        if (user == null){
            throw new IllegalArgumentException("User not found");
        }

        if (user.getRole() == null){
           user.setRole(new ArrayList<>());
        }

        user.getRole().add(role);
        userRepository.save(user);

        return roleMapper.toAddRoleUserResponse(user);
    }
}
