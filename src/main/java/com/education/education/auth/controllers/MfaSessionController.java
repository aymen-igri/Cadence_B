package com.education.education.auth.controllers;

import com.education.education.auth.enums.EMfaType;
import com.education.education.auth.services.MfaSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mfa")
@AllArgsConstructor
public class MfaSessionController {

    private final MfaSessionService mfaSessionService;

    public ResponseEntity<?> triggerMfa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam EMfaType type
    ){
        mfaSessionService.sendMfaCode(userDetails, type);
        return ResponseEntity.ok(Map.of("message", "verification code sent via " + type));
    }
}
