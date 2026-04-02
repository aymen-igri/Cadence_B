package com.education.education.user.role.mappers;

import com.education.education.user.role.dto.request.NewRoleRequest;
import com.education.education.user.role.dto.response.AddRoleToUserResponse;
import com.education.education.user.role.dto.response.NewRoleResponse;
import com.education.education.user.role.entities.Role;
import com.education.education.user.user.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public Role toRole(NewRoleRequest reuqest){
        Role role = new Role();
        role.setRole(reuqest.role());
        return role;
    }

    public NewRoleResponse toNewRoleResponse(Role role){
        return new NewRoleResponse(role.getId(), role.getRole(), role.getCreatedAt());
    }
    
    public AddRoleToUserResponse toAddRoleUserResponse(User user){
        List<String> currentRoles = user.getRole().stream()
                .map(Role::getRole)
                .collect(Collectors.toList());
                
        String lastRole = currentRoles.isEmpty() ? null : currentRoles.get(currentRoles.size() - 1);
        
        return new AddRoleToUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                lastRole
        );
    }
}
