package com.example.PaymentService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "farmer-service", url = "http://localhost:8081")
public interface FarmerFeign {
	
    @GetMapping("/farmers/name/{id}")
    String getFarmerName(@PathVariable Long id,@RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);
    
}