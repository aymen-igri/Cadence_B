package com.education.education.auth.controllers;

import com.education.education.auth.enums.EMfaType;
import com.education.education.auth.services.MfaSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mfa")
@AllArgsConstructor
public class MfaSessionController {

    private final MfaSessionService mfaSessionService;


    @PostMapping("/trigger")
    @PreAuthorize("hasRole('PRE_AUTH')")
    public ResponseEntity<?> triggerMfa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam EMfaType type
    ){
        mfaSessionService.sendMfaCode(userDetails, type);
        return ResponseEntity.ok(Map.of("message", "verification code sent via " + type));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('PRE_AUTH')")
    public ResponseEntity<?> verifyAndSwap(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String code,
            @RequestParam EMfaType type
    ){
        boolean isValid = mfaSessionService.verifyMfa(userDetails, code, type);
        if (!isValid) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired code")); // dev mode, need improvement to tell if the code is expired or something else is breaking
        }

        return ResponseEntity.ok(mfaSessionService.generateFinalToken(userDetails));
    }
}
