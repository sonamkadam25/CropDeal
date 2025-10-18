package com.example.PaymentService.repository;



import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PaymentService.entity.Payment;
import com.example.PaymentService.entity.Receipt;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByFarmerId(Long farmerId);

	List<Payment> findByDealerId(Long dealerId);
}


