package com.example.DealerService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DealerService.entity.Dealer;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

	Optional<Dealer> findByEmail(String email);
}
