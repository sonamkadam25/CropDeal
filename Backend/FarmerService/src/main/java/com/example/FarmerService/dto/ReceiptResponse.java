package com.example.FarmerService.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ReceiptResponse {
    private Long receiptId;
    private String cropName;
    private Integer quantity;
    private Double totalAmount;
    private Date date;
	public Long getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}
	public String getCropName() {
		return cropName;
	}
	public void setCropName(String cropName) {
		this.cropName = cropName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ReceiptResponse(Long receiptId, String cropName, Integer quantity, Double totalAmount, Date date) {
		super();
		this.receiptId = receiptId;
		this.cropName = cropName;
		this.quantity = quantity;
		this.totalAmount = totalAmount;
		this.date = date;
	}
	public ReceiptResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
