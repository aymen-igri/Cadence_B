package com.education.education.auth.controllers;

import com.education.education.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.hibernate.boot.internal.Abstract;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refreshToken")
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        authService.refreshToken(req, res);
    }
}
