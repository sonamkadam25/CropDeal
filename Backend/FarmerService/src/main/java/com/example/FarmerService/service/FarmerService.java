package com.example.FarmerService.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


//import com.example.FarmerService.exception.InvalidCredentialsException;
import com.example.FarmerService.dto.FarmerRequest;
import com.example.FarmerService.dto.FarmerResponse;
import com.example.FarmerService.dto.FarmerRequest;
import com.example.FarmerService.dto.FarmerResponse;
import com.example.FarmerService.dto.LoginRequest;
import com.example.FarmerService.dto.LoginResponse;
import com.example.FarmerService.dto.ReceiptResponse;
import com.example.FarmerService.dto.RegisterRequest;
import com.example.FarmerService.entity.BankDetails;
import com.example.FarmerService.entity.Farmer;
import com.example.FarmerService.entity.OtpEntry;
import com.example.FarmerService.entity.Receipt;
import com.example.FarmerService.enums.Role;
import com.example.FarmerService.feign.AuthFeignClient;
import com.example.FarmerService.repository.FarmerRepository;
import com.example.FarmerService.repository.ReceiptRepository;
import com.example.FarmerService.repository.OtpRepository;
import com.example.FarmerService.dto.ResetPasswordRequest;

import org.springframework.transaction.annotation.Transactional;
import feign.FeignException;

@Service
public class FarmerService {
    @Autowired
    private FarmerRepository farmerRepo;

    @Autowired
    private ReceiptRepository receiptRepo;
    
    @Autowired
    AuthFeignClient authFeignClient;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private OtpRepository otpRepository;
   
    public FarmerResponse register(FarmerRequest req) {
        try {
            // First, register with Auth Service
            RegisterRequest authReq = new RegisterRequest();
            authReq.setName(req.getName());
            authReq.setEmail(req.getEmail());
            authReq.setPassword(req.getPassword());  
            authReq.setRole(Role.FARMER);

            authFeignClient.register(authReq); // may throw FeignException.BadRequest

            // If successful, now save locally
            Farmer farmer = new Farmer();
            farmer.setName(req.getName());
            farmer.setEmail(req.getEmail());
            farmer.setPhone(req.getPhone());
            farmerRepo.save(farmer);

            return mapToResponse(farmer);

        } catch (FeignException.BadRequest e) {
            // Handle specific error from Auth Service
            throw new RuntimeException("Email already exists!");
        } 
    }

    
    public LoginResponse login(LoginRequest request) {
        try {
            return authFeignClient.login(request);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
  
    public List<FarmerResponse> getAllFarmers() {
        List<Farmer> farmers = farmerRepo.findAll();
        return farmers.stream()
                      .map(this::mapToResponse)
                      .collect(Collectors.toList());
    }
    

    
    private FarmerResponse mapToResponse(Farmer farmer) {
        FarmerResponse res = new FarmerResponse();
        res.setId(farmer.getId());
        res.setName(farmer.getName() != null ? farmer.getName() : "N/A");
        res.setEmail(farmer.getEmail() != null ? farmer.getEmail() : "N/A");
        res.setPhone(farmer.getPhone() != null ? farmer.getPhone() : "N/A");

        // Fix for potential null value in `active` field
        res.setActive(farmer.isActive() ? farmer.isActive() : false);

        return res;
    }
    
    public boolean updateFarmerStatus(Long id, boolean active) {
        Optional<Farmer> optionalFarmer = farmerRepo.findById(id);
        if (optionalFarmer.isPresent()) {
            Farmer farmer = optionalFarmer.get();
            farmer.setActive(active);
            farmerRepo.save(farmer);
            return true;
        } else {
            return false;
        }
    }

    public FarmerResponse updateProfile(Long id, FarmerRequest req) {
        Farmer farmer = farmerRepo.findById(id).orElseThrow();
        farmer.setName(req.getName());
        farmer.setEmail(req.getEmail());
        farmer.setPhone(req.getPhone());
        farmerRepo.save(farmer);
        
        
        return mapToResponse(farmer);
    }

    public void addBankDetails(Long id, BankDetails bankDetails) {
        Farmer farmer = farmerRepo.findById(id).orElseThrow();
        farmer.setBankDetails(bankDetails);
        farmerRepo.save(farmer);
    }

    public List<ReceiptResponse> getReceipts(Long id) {
        List<Receipt> receipts = receiptRepo.findByCropFarmerId(id);
        return receipts.stream().map(r -> {
            ReceiptResponse res = new ReceiptResponse();
            res.setReceiptId(r.getId());
            res.setCropName(r.getCrop().getName());
            res.setQuantity(r.getQuantity());
            res.setTotalAmount(r.getTotalAmount());
            res.setDate(r.getDate());
            return res;
        }).collect(Collectors.toList());
    }

    public String getFarmerNameById(Long id) {
        Farmer farmer = farmerRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
        return farmer.getName();
    }

 
    @Transactional
    public boolean sendOtpToEmail(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5); // Set OTP expiry

        try {
            // STEP 1: Delete any old OTP for this email
            otpRepository.deleteByEmail(email); // 💡 MUST come before save

            // STEP 2: Save the new OTP entry
            OtpEntry entry = new OtpEntry(email, otp, expiry);
            otpRepository.save(entry);

            // STEP 3: Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Reset Password OTP - CropDeal");
            message.setText("Your OTP for password reset is: " + otp + "\n\nValid for 5 minutes.");
            mailSender.send(message);

            System.out.println("OTP " + otp + " sent and saved for email: " + email);
            
            System.out.println("Stored OTP: " + entry.getOtp());
            System.out.println("Expiry: " + entry.getExpiryTime());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean verifyOtp(String email, String enteredOtp) {
        Optional<OtpEntry> optional = otpRepository.findByEmail(email);
        if (optional.isEmpty()) return false;

        OtpEntry entry = optional.get();
        
        boolean isValid = entry.getOtp().equals(enteredOtp);

        // Optional: Also check expiry
        if (entry.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false; // Expired
        }

        return isValid;
    }
    
    public void clearOtp(String email) {
        otpRepository.deleteByEmail(email);
    }
    
    @Transactional
    public boolean resetPassword(String email, String newPassword, String enteredOtp) {
        System.out.println("Reset password called for " + email + " with OTP " + enteredOtp);

        if (!verifyOtp(email, enteredOtp)) {
            System.out.println("OTP verification failed.");
            return false;
        }

        try {
            ResetPasswordRequest req = new ResetPasswordRequest();
            req.setEmail(email);
            req.setNewPassword(newPassword);
            authFeignClient.resetPassword(req); // actual password reset happens here
            clearOtp(email);
            return true;
        } catch (Exception e) {
            System.out.println("Error calling Auth Service: " + e.getMessage());
            return false;
        }
    }
}
