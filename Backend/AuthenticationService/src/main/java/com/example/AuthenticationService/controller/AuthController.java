package com.example.AuthenticationService.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.AuthenticationService.dto.LoginRequest;
import com.example.AuthenticationService.dto.LoginResponse;
import com.example.AuthenticationService.dto.RegisterRequest;
import com.example.AuthenticationService.dto.ResetPasswordRequest;
import com.example.AuthenticationService.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return "Registered Successfully!";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
        boolean success = authService.resetPassword(req.getEmail(), req.getNewPassword());
        if (success) return ResponseEntity.ok("Password reset successful");
        else return ResponseEntity.badRequest().body("User not found");
    }

}
