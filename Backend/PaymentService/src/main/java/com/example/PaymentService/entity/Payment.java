package com.example.PaymentService.entity;
import java.time.LocalDateTime;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dealerId;
    private Long farmerId;
    private Double amount;
    private LocalDateTime timestamp;
    private String status; // "SUCCESS", "FAILED"

    @OneToOne(cascade = CascadeType.ALL)
    private Receipt receipt;

    @OneToOne(cascade = CascadeType.ALL)
    private Invoice invoice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public Long getFarmerId() {
		return farmerId;
	}

	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Payment(Long id, Long dealerId, Long farmerId, Double amount, LocalDateTime timestamp, String status,
			Receipt receipt, Invoice invoice) {
		super();
		this.id = id;
		this.dealerId = dealerId;
		this.farmerId = farmerId;
		this.amount = amount;
		this.timestamp = timestamp;
		this.status = status;
		this.receipt = receipt;
		this.invoice = invoice;
	}

	public Payment() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}

