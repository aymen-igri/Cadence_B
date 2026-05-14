package com.education.education.auth.controllers;

import com.education.education.auth.deo.responses.SignUpDTOResponse;
import com.education.education.auth.services.AuthService;
import com.education.education.user.passwordResetToken.dto.request.PasswordResetReq;
import com.education.education.user.passwordResetToken.services.PasswordResetTokenService;
import com.education.education.user.user.dto.request.AddUserRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Generation Engine", description = "Endpoints for generating study plans")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetTokenService passwordResetTokenService;

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

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(
            @RequestParam String identifier
    ){
        passwordResetTokenService.initialRecovery(identifier);
        return ResponseEntity.ok("Recovery email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody PasswordResetReq req
    ){
        passwordResetTokenService.resetWithToken(req.token(), req.newPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}
