package com.example.FarmerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FarmerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmerServiceApplication.class, args);
	}
}
