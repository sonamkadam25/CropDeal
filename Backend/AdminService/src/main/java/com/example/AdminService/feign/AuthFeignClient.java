package com.example.AdminService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.AdminService.dto.LoginRequest;
import com.example.AdminService.dto.LoginResponse;
import com.example.AdminService.dto.RegisterRequest;

@FeignClient(name = "auth-service" , url = "http://localhost:8080")
public interface AuthFeignClient {
    @PostMapping("/auth/register")
    void register(@RequestBody RegisterRequest request);

    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);
}
