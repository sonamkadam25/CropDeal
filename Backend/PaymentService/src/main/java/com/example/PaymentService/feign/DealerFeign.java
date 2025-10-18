package com.example.PaymentService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "dealer-service", url = "http://localhost:8083")
public interface DealerFeign {
	
    @GetMapping("/dealer/name/{id}")
    String getDealerName(@PathVariable Long id,@RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);
    
}
