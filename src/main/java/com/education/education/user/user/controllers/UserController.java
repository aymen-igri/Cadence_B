package com.education.education.user.user.controllers;

import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<AddUserResponse> addUser(
            @Valid @RequestBody AddUserRequest userRequest,
            @RequestParam String roleName
    ){
        return ResponseEntity.ok(userService.createUser(userRequest,roleName));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.profile(userDetails));
    }
}
