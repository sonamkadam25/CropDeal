package com.example.FarmerService.controller;

import java.util.List;
import java.util.Map;

import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.FarmerService.dto.FarmerRequest;
import com.example.FarmerService.dto.FarmerResponse;
import com.example.FarmerService.dto.LoginRequest;
import com.example.FarmerService.dto.LoginResponse;
import com.example.FarmerService.entity.BankDetails;
import com.example.FarmerService.service.FarmerService;

@RestController
@RequestMapping("/farmers")
public class FarmerController {
    @Autowired
    private FarmerService farmerService;
    
   // @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody FarmerRequest request) {
        try {
            return ResponseEntity.ok(farmerService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(farmerService.login(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<FarmerResponse>> getAllFarmers() {
        try {
            return ResponseEntity.ok(farmerService.getAllFarmers());
        } catch (Exception e) {
            e.printStackTrace(); // Print to server logs
            return ResponseEntity.status(500).build(); // Or send custom message
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateFarmerStatus(@PathVariable Long id, @RequestParam boolean active) {
        boolean updated = farmerService.updateFarmerStatus(id, active);
        if (updated) {
            return ResponseEntity.ok("Farmer status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Farmer not found.");
        }
    }

    @PreAuthorize("hasRole('FARMER') or hasRole('ADMIN')")
    @PutMapping("/{id}/update")
    public ResponseEntity<FarmerResponse> updateProfile(@PathVariable Long id, @RequestBody FarmerRequest request) {
        return ResponseEntity.ok(farmerService.updateProfile(id, request));
     }


    @PreAuthorize("hasRole('FARMER')")
    @PostMapping("/{id}/bank")
    public ResponseEntity<String> addBankDetails(@PathVariable Long id, @RequestBody BankDetails bankDetails) {
        farmerService.addBankDetails(id, bankDetails);
        return ResponseEntity.ok("Bank details added successfully.");
    }
    
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARMER') or hasRole('DEALER')")
    @GetMapping("/name/{id}")
    public ResponseEntity<String> getFarmerName(@PathVariable Long id) {
        String name = farmerService.getFarmerNameById(id);
        return ResponseEntity.ok(name);
    }
    
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        boolean sent = farmerService.sendOtpToEmail(email);
        if (sent) {
            return ResponseEntity.ok("OTP sent to " + email);
        } else {
            return ResponseEntity.internalServerError().body("Failed to send OTP");
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        boolean success = farmerService.resetPassword(email, newPassword, otp);
        if (success) {
            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid or expired OTP, or email not found");
        }
    }

}