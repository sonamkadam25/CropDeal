package com.example.DealerService.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.DealerService.entity.Crop;

@FeignClient(name = "farmer-service", url = "http://localhost:8081")
public interface CropFeignClient {
    @PostMapping("/farmers/crops/subscribe")
    void subscribeCrop(@RequestParam Long dealerId, @RequestParam Long cropId, @RequestParam String dealerEmail, @RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);

    @GetMapping("/farmers/crops/all")
	List<Crop> getAllCrops(@RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);
    
    
}
