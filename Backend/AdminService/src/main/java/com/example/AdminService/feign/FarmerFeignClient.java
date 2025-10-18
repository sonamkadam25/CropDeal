package com.example.AdminService.feign;

import com.example.AdminService.dto.FarmerDto;
import com.example.AdminService.dto.FarmerRequest;
import com.example.AdminService.dto.FarmerResponse;
import com.example.AdminService.entity.Crop;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "farmer-service", url = "http://localhost:8081")
public interface FarmerFeignClient {
	
	@GetMapping("/farmers/all")
	    List<FarmerDto> getAllFarmers(
	        @RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role
	    );

    @PutMapping("/farmers/{id}/status")
    void updateFarmerStatus(@PathVariable("id") Long id, @RequestParam("active") boolean active, @RequestHeader("x-auth-user") String username,
	        @RequestHeader("x-auth-user-role") String role);
    
    @PutMapping("/farmers/{id}/update")
    FarmerResponse updateFarmerProfile
    	(@PathVariable("id") Long id,
    	 @RequestBody FarmerRequest farmerRequest,
    	 @RequestHeader("x-auth-user") String username,
	     @RequestHeader("x-auth-user-role") String role
	     
    	);
    
    @GetMapping("/farmers/crops/all")
   	List<Crop> getAllCrops(
   			@RequestHeader("x-auth-user") String username,
   	        @RequestHeader("x-auth-user-role") String role
   	      );
    
    @DeleteMapping("/farmers/crops/delete/{cropId}")
    void deleteCropById(
        @PathVariable("cropId") Long cropId,
        @RequestHeader("x-auth-user") String username,
        @RequestHeader("x-auth-user-role") String role
    );

}
