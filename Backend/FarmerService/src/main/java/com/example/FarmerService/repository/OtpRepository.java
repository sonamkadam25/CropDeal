package com.example.FarmerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.FarmerService.entity.OtpEntry;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntry, Long> {
    Optional<OtpEntry> findByEmail(String email);
    void deleteByEmail(String email);
}
