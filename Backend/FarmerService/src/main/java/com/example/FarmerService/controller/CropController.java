package com.example.FarmerService.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.FarmerService.dto.CropPostResponse;
import com.example.FarmerService.dto.CropRequest;
import com.example.FarmerService.entity.Crop;
import com.example.FarmerService.service.CropService;

@RestController
@RequestMapping("/farmers/crops")
public class CropController {
    @Autowired
    private CropService cropService;
    
    @PreAuthorize("hasRole('FARMER')")
    @PostMapping("/{farmerId}/post")
    public ResponseEntity<CropPostResponse> postCrop(@PathVariable Long farmerId, @RequestBody CropRequest cropRequest) {
    	CropPostResponse postedCrop = cropService.postCropAndNotifyDealers(farmerId, cropRequest);
        return ResponseEntity.ok(postedCrop);
    }

    @GetMapping("/{farmerId}")
    public ResponseEntity<List<Crop>> getFarmerCrops(@PathVariable Long farmerId) {
        return ResponseEntity.ok(cropService.getCropsByFarmerId(farmerId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Crop>> getAllCrops() {
        List<Crop> crops = cropService.getAllCrops();
        return ResponseEntity.ok(crops);
    }
    
    @GetMapping("/allForHomePage")
    public ResponseEntity<List<Crop>> getAllCropForHomePage() {
        List<Crop> crops = cropService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    @PreAuthorize("hasRole('DEALER')")
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToCrop(@RequestParam Long dealerId, @RequestParam Long cropId, @RequestParam String dealerEmail) {
        cropService.subscribeCrop(dealerId, cropId, dealerEmail);
        return ResponseEntity.ok("Subscribed successfully to crop ID: " + cropId);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{cropId}")
    public ResponseEntity<String> deleteCropAsAdmin(@PathVariable Long cropId) {
        cropService.deleteCropByAdmin(cropId);
        return ResponseEntity.ok("Crop with ID " + cropId + " has been deleted by admin.");
    }


}
