package com.example.FarmerService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FarmerService.entity.Receipt;
import com.example.FarmerService.repository.ReceiptRepository;

@Service
public class ReceiptService {
    @Autowired
    private ReceiptRepository receiptRepo;

    public List<Receipt> getAllByFarmerId(Long farmerId) {
        return receiptRepo.findByCropFarmerId(farmerId);
    }
}

