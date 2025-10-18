package com.example.FarmerService.dto;

import lombok.Data;

@Data
public class CropRequest {

	private String type; // vegetable/fruit
    private String name;
    private Integer quantity;
    private Double pricePerUnit;
	private String location;
	
    public CropRequest(String type, String name, Integer quantity, Double pricePerUnit, String location) {
		super();
		this.type = type;
		this.name = name;
		this.quantity = quantity;
		this.pricePerUnit = pricePerUnit;
		this.location = location;
	}
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
    public Double getPricePerUnit() {
		return pricePerUnit;
	}
	public void setPricePerUnit(Double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}
	public CropRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}

