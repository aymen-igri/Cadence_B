package com.education.education.auth.controllers;

import com.education.education.auth.deo.responses.SignUpDTOResponse;
import com.education.education.auth.services.AuthService;
import com.education.education.user.user.dto.request.AddUserRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Generation Engine", description = "Endpoints for generating study plans")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpDTOResponse> signUp(
            @Valid @RequestBody AddUserRequest newUser
    ){
        return ResponseEntity.ok(authService.signUp(newUser));
    }

    @PostMapping("/refreshToken")
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        authService.refreshToken(req, res);
    }
}
