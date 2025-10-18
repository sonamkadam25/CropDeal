package com.example.FarmerService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FarmerService.entity.BankDetails;
import com.example.FarmerService.repository.BankRepository;

@Service
public class BankService {
    @Autowired
    private BankRepository bankRepo;

    public BankDetails save(BankDetails details) {
        return bankRepo.save(details);
    }
}
