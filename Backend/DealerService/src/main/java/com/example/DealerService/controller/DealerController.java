package com.example.DealerService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DealerService.dto.DealerRequest;
import com.example.DealerService.dto.DealerResponse;
import com.example.DealerService.dto.LoginRequest;
import com.example.DealerService.dto.LoginResponse;
import com.example.DealerService.entity.BankDetails;
import com.example.DealerService.service.DealerService;
import com.example.DealerService.entity.Crop;
import com.example.DealerService.entity.Dealer;

@RestController
@RequestMapping("/dealer")
public class DealerController {
    @Autowired
    DealerService service;

    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DealerRequest req) {
        try {
            return ResponseEntity.ok(service.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<DealerResponse>> getAllDealers() {
        try {
            return ResponseEntity.ok(service.getAllDealers());
        } catch (Exception e) {
            e.printStackTrace(); // Print to server logs
            return ResponseEntity.status(500).build(); // Or send custom message
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateFarmerStatus(@PathVariable Long id, @RequestParam boolean active) {
        boolean updated = service.updateFarmerStatus(id, active);
        if (updated) {
            return ResponseEntity.ok("Dealer status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Farmer not found.");
        }
    }

    @PreAuthorize("hasRole('DEALER') or hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public DealerResponse update(@PathVariable Long id, @RequestBody DealerRequest req) {
        return service.updateProfile(id, req);
    }

    @PreAuthorize("hasRole('DEALER')")
    @PostMapping("/bank/{id}")
    public String addBank(@PathVariable Long id, @RequestBody BankDetails bank) {
        return service.addBankDetails(id, bank);
    }

    @PreAuthorize("hasRole('DEALER')")
    @PostMapping("/subscribe")
    public String subscribeCrop(@RequestParam Long dealerId, @RequestParam Long cropId, @RequestParam String dealerEmail) {
        return service.subscribeToCrop(dealerId, cropId, dealerEmail);
    }
    
    @GetMapping("/allcrops")
    public ResponseEntity<List<Crop>> getAllCrop() {
        List<Crop> croplist = service.getAllCrop();
        return ResponseEntity.ok(croplist);
    }
    
    @PreAuthorize("hasRole('DEALER') or hasRole('ADMIN')")
    @GetMapping("/name/{id}")
    public ResponseEntity<String> getDealerName(@PathVariable Long id) {
        try {
            String dealerName = service.getDealerNameById(id);
            return ResponseEntity.ok(dealerName);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PreAuthorize("hasRole('DEALER') or hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getDealerId(@PathVariable String email) {
        try {
            Long dealerId = service.getDealerIdByEmail(email);
            return ResponseEntity.ok(dealerId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDealerById(@PathVariable Long id) {
        try {
            Dealer dealer = service.getDealerById(id);
            return ResponseEntity.ok(dealer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}

