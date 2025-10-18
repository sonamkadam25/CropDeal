package com.example.FarmerService.entity;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Crop crop;

    private Integer quantity;
    private Double totalAmount;
    public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	private Date date;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Crop getCrop() {
		return crop;
	}
	public void setCrop(Crop crop) {
		this.crop = crop;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(double d) {
		this.quantity = (int) d;
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

	public Receipt(Long id, Crop crop, Integer quantity, Double totalAmount, Date date) {
		super();
		this.id = id;
		this.crop = crop;
		this.quantity = quantity;
		this.totalAmount = totalAmount;
		this.date = date;
	}
	public Receipt() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setDate(String string) {
		// TODO Auto-generated method stub
		
	}
	
	public void setDate(LocalDate now) {
		// TODO Auto-generated method stub
		
	}

    
    
}

