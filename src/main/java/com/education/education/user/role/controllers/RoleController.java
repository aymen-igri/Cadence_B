package com.education.education.user.role.controllers;

import com.education.education.user.role.dto.request.AddRoleToUserRequest;
import com.education.education.user.role.dto.request.NewRoleRequest;
import com.education.education.user.role.dto.response.NewRoleResponse;
import com.education.education.user.role.services.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/add")
    public ResponseEntity<NewRoleResponse> addRole(
            @Valid @RequestBody NewRoleRequest role
    ){
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PostMapping("/add-to-user")
    public ResponseEntity<?> addRoleToUser(
            @Valid @RequestBody AddRoleToUserRequest role
    ){
        return ResponseEntity.ok(roleService.addRoleToUser(role));
    }
}
