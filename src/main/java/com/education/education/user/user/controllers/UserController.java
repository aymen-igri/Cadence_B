package com.education.education.user.user.controllers;

import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.request.UpdateUserDataReq;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.dto.response.UpdateUserDataRes;
import com.education.education.user.user.dto.response.UserProfileRes;
import com.education.education.user.user.services.ImageService;
import com.education.education.user.user.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    @PostMapping("/add")
    public ResponseEntity<AddUserResponse> addUser(
            @Valid @RequestBody AddUserRequest userRequest,
            @RequestParam String roleName
    ){
        return ResponseEntity.ok(userService.createUser(userRequest,roleName));
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('GENERAL_USER', 'ADMIN')")
    public ResponseEntity<UpdateUserDataRes> updateUserData(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserDataReq updateReq
    ){
        return ResponseEntity.ok(userService.updateUserData(userDetails, updateReq));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('GENERAL_USER', 'ADMIN')")
    public ResponseEntity<UserProfileRes> profile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.profile(userDetails));
    }

    @PatchMapping("/changePFP")
    @PreAuthorize("hasAnyRole('GENERAL_USER', 'ADMIN')")
    public ResponseEntity<?> changePFP(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Please select a picture to uploade");

        String imageURL = imageService.uploadeImage(file);

        userService.updatePFP(userDetails, imageURL);

        return ResponseEntity.ok(Map.of(
                "message", "picture profile updated",
                "imageURL", imageURL
        ));
    }
}
