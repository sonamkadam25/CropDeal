package com.example.PaymentService.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PaymentService.entity.Invoice;
import com.example.PaymentService.entity.Payment;
import com.example.PaymentService.entity.Receipt;
import com.example.PaymentService.feign.DealerFeign;
import com.example.PaymentService.feign.FarmerFeign;
import com.example.PaymentService.repository.PaymentRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private DealerFeign dealerClient;

    @Autowired
    private FarmerFeign farmerClient;
    
    @Autowired
    private HttpServletRequest request;

    public String processPayment(Long dealerId, Long farmerId, Double amount, String cropName) {
        // Step 1: Fetch names from other services if needed
    	
    	String username = request.getHeader("x-auth-user");
 	    String role = request.getHeader("x-auth-user-role");

 	    if (username == null || role == null) {
 	        throw new RuntimeException("Missing authentication headers");
 	    }
    	
        String dealerName = dealerClient.getDealerName(dealerId, username, role);
        String farmerName = farmerClient.getFarmerName(farmerId, username, role);

        // Step 2: Create receipt & invoice
        Receipt receipt = new Receipt(farmerName, amount, LocalDateTime.now(), cropName);
        Invoice invoice = new Invoice(dealerName, amount, LocalDateTime.now(), cropName);
        
        System.out.println("DEBUG - FarmerName: " + farmerName);
        System.out.println("DEBUG - Amount: " + amount);
        System.out.println("DEBUG - CropName: " + cropName);


        // Step 3: Save payment
        Payment payment = new Payment();
        payment.setDealerId(dealerId);
        payment.setFarmerId(farmerId);
        payment.setAmount(amount);
        payment.setTimestamp(LocalDateTime.now());
        payment.setStatus("SUCCESS");
        payment.setReceipt(receipt);
        payment.setInvoice(invoice);

        paymentRepo.save(payment);

        return "Payment transferred successfully.";
    }
    
    public List<Receipt> getReceiptsForFarmer(Long farmerId) {
        List<Payment> payments = paymentRepo.findByFarmerId(farmerId);
        List<Receipt> receipts = payments.stream()
                                         .map((Payment p) -> p.getReceipt())
                                         .collect(Collectors.toList());
        return receipts;
    }



    public List<Invoice> getInvoicesForDealer(Long dealerId) {
    	
    	 List<Payment> payments = paymentRepo.findByDealerId(dealerId);
         List<Invoice> invoices = payments.stream()
                                          .map((Payment p) -> p.getInvoice())
                                          .collect(Collectors.toList());
         return invoices;
    	
    }
}

