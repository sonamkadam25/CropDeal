package com.example.FarmerService.dto;

import java.util.List;

import com.example.FarmerService.entity.Crop;

public class CropPostResponse {
    private Crop crop;
    private List<Long> notifiedDealers;

    public CropPostResponse(Crop crop, List<Long> notifiedDealers) {
        this.crop = crop;
        this.notifiedDealers = notifiedDealers;
    }

    // Getters and Setters
    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public List<Long> getNotifiedDealers() {
        return notifiedDealers;
    }

    public void setNotifiedDealers(List<Long> notifiedDealers) {
        this.notifiedDealers = notifiedDealers;
    }
}
