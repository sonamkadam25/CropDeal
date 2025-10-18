package com.example.AdminService.controller;

import com.example.AdminService.dto.AdminRequest;
import com.example.AdminService.dto.AdminResponse;
import com.example.AdminService.dto.DealerDto;
import com.example.AdminService.dto.DealerRequest;
import com.example.AdminService.dto.DealerResponse;
import com.example.AdminService.dto.FarmerDto;
import com.example.AdminService.dto.FarmerRequest;
import com.example.AdminService.dto.FarmerResponse;
import com.example.AdminService.dto.LoginRequest;
import com.example.AdminService.dto.LoginResponse;
import com.example.AdminService.service.AdminService;
import com.example.AdminService.entity.Crop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdminRequest req) {
        try {
            return ResponseEntity.ok(adminService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return adminService.login(req);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/farmers")
    @Operation(summary="Get all Farmers")
    public ResponseEntity<List<FarmerDto>> getAllFarmers() {
        return ResponseEntity.ok(adminService.getAllFarmers());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/farmer/{id}/status")
    public ResponseEntity<String> updateFarmerStatus(@PathVariable Long id, @RequestParam boolean active) {
        adminService.updateFarmerStatus(id, active);
        return ResponseEntity.ok("Farmer status updated successfully.");
       // return ResponseEntity.ok("Farmer status updated successfully.").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/farmer/{id}/update")
    public ResponseEntity<FarmerResponse> updateFarmerProfile(@PathVariable Long id, @RequestBody FarmerRequest farmerRequest) {
        FarmerResponse response = adminService.updateFarmerProfile(id, farmerRequest);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dealers")
    public ResponseEntity<List<DealerDto>> getAllDealers() {
    	System.out.println("Inside /admin/dealers API");
        return ResponseEntity.ok(adminService.getAllDealers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/dealer/{id}/status")
    public ResponseEntity<String> updateDealerStatus(@PathVariable Long id, @RequestParam boolean active) {
        adminService.updateDealerStatus(id, active);
        return ResponseEntity.ok("Dealer status updated successfully!!");
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/dealer/{id}/update")
    public ResponseEntity<DealerResponse> updateDealerProfile(@PathVariable Long id, @RequestBody DealerRequest dealerRequest) {
        DealerResponse response = adminService.updateDealerProfile(id, dealerRequest);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/allcrops")
    public ResponseEntity<List<Crop>> getAllCrop() {
        List<Crop> croplist = adminService.getAllCrop();
        return ResponseEntity.ok(croplist);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCrop/{cropId}")
    public ResponseEntity<String> deleteCrop(@PathVariable Long cropId) {
        adminService.deleteCropById(cropId);
        return ResponseEntity.ok("Crop with ID " + cropId + " deleted successfully.");
    }


}
