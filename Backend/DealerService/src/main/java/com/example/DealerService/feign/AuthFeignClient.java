package com.example.DealerService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.DealerService.dto.LoginRequest;
import com.example.DealerService.dto.LoginResponse;
import com.example.DealerService.dto.RegisterRequest;

@FeignClient(name = "auth-service" , url = "http://localhost:8080")
public interface AuthFeignClient {
    @PostMapping("/auth/register")
    void register(@RequestBody RegisterRequest request);
   // String register(@RequestBody RegisterRequest request);


    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);
}
