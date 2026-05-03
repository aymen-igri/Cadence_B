package com.education.education.auth.controllers;

import com.education.education.auth.deo.requests.MfaAppReq;
import com.education.education.auth.deo.responses.MfaSetupRes;
import com.education.education.auth.enums.EMfaType;
import com.education.education.auth.services.MfaSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mfa")
@AllArgsConstructor
public class MfaSessionController {

    private final MfaSessionService mfaSessionService;


    @PostMapping("/email/trigger")
    @PreAuthorize("hasRole('PRE_AUTH')")
    public ResponseEntity<?> triggerMfa(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        mfaSessionService.sendMfaEmailCode(userDetails);
        return ResponseEntity.ok(Map.of("message", "verification code sent via " + "email"));
    }

    @GetMapping("/app/setUp")
    @PreAuthorize("hasRole('GENERAL_USER')")
    public ResponseEntity<MfaSetupRes> initialSetUp(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Map<String, String> response = mfaSessionService.setupTotp(userDetails);
        return ResponseEntity.ok( new MfaSetupRes(
                    response.get("secretKey"),
                    response.get("qrUrl")
                )
        );
    }

    @PostMapping("/app/confirm")
    @PreAuthorize("hasRole('GENERAL_USER')")
    public ResponseEntity<?> confirmSetUp(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MfaAppReq request
            ){

        boolean valid = mfaSessionService.confirmTotpSetup(userDetails, request.code());

        if (!valid) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid verification code. Setup failed."));
        }else{
            return ResponseEntity.ok(mfaSessionService.generateFinalToken(userDetails));
        }
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
