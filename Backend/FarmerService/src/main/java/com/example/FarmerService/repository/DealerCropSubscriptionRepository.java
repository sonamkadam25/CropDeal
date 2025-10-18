package com.example.FarmerService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FarmerService.entity.Crop;
import com.example.FarmerService.entity.DealerCropSubscription;

public interface DealerCropSubscriptionRepository extends JpaRepository<DealerCropSubscription, Long> {
    List<DealerCropSubscription> findByCrop_Type(String type);

	void deleteByCrop(Crop crop);
}
