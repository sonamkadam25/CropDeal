package com.example.FarmerService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.FarmerService.dto.LoginRequest;
import com.example.FarmerService.dto.LoginResponse;
import com.example.FarmerService.dto.RegisterRequest;
import com.example.FarmerService.dto.ResetPasswordRequest;

import feign.Headers;

@FeignClient(name = "auth-service", url = "http://localhost:8080")
public interface AuthFeignClient {

    @PostMapping("/auth/register")
    @Headers("Content-Type: application/json")
    String register(@RequestBody RegisterRequest request);

    @PostMapping("/auth/login")
    @Headers("Content-Type: application/json")
    LoginResponse login(@RequestBody LoginRequest request);
    
    @PostMapping("/auth/reset-password")
    String resetPassword(@RequestBody ResetPasswordRequest request);

}


