package com.example.FarmerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FarmerService.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

	Object findByEmail(String email);
	}
