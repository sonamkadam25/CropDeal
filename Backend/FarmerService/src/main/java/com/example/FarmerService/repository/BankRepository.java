package com.example.FarmerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FarmerService.entity.BankDetails;

public interface BankRepository extends JpaRepository<BankDetails, Long> {}