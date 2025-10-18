package com.example.DealerService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {
    @PostMapping("/payment/pay")
    String initiatePayment(@RequestParam Long dealerId, @RequestParam Double amount);
}
