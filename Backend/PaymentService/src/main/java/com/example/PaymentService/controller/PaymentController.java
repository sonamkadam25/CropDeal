package com.example.PaymentService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PaymentService.entity.Invoice;
import com.example.PaymentService.entity.Receipt;
import com.example.PaymentService.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PreAuthorize("hasRole('DEALER')")
    @PostMapping("/pay")
    public ResponseEntity<String> makePayment(@RequestParam Long dealerId,
                                              @RequestParam Double amount,
                                              @RequestParam Long farmerId,
                                              @RequestParam String cropName) {
        return ResponseEntity.ok(paymentService.processPayment(dealerId, farmerId, amount, cropName));
    }

    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/receipt/{farmerId}")
    public ResponseEntity<List<Receipt>> getFarmerReceipts(@PathVariable Long farmerId) {
        return ResponseEntity.ok(paymentService.getReceiptsForFarmer(farmerId));
    }

    @PreAuthorize("hasRole('DEALER')")
    @GetMapping("/invoice/{dealerId}")
    public ResponseEntity<List<Invoice>> getDealerInvoices(@PathVariable Long dealerId) {
        return ResponseEntity.ok(paymentService.getInvoicesForDealer(dealerId));
    }
}

