package com.example.FarmerService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FarmerService.entity.Crop;

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByFarmerId(Long farmerId);
}