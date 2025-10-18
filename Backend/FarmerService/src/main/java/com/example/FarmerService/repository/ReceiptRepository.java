package com.example.FarmerService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FarmerService.entity.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByCropFarmerId(Long farmerId);
}
