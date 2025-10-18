package com.example.AdminService.feign;

import com.example.AdminService.dto.DealerDto;
import com.example.AdminService.dto.DealerRequest;
import com.example.AdminService.dto.DealerResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "dealer-service", url = "http://localhost:8083")
public interface DealerFeignClient {
	
    @GetMapping("/dealer/all")
    List<DealerDto> getAllDealers(
    		@RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role
	        );

    @PutMapping("/dealer/{id}/status")
    void updateDealerStatus(@PathVariable("id") Long id, @RequestParam("active") boolean active,@RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);
    
    @PutMapping("/dealer/update/{id}")
    DealerResponse updateDealerProfile(@PathVariable("id") Long id, @RequestBody DealerRequest dealerRequest,@RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);
}