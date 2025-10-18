package com.example.FarmerService.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Dealer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    
    @Column(nullable = false)
    private Boolean active = true;

    public boolean isActive() {
        return Boolean.TRUE.equals(active); // avoids NPE
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    @OneToOne(cascade = CascadeType.ALL)
    private BankDetails bankDetails;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public BankDetails getBankDetails() {
		return bankDetails;
	}
	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}
	public Dealer(Long id, String name, String email, String phone, BankDetails bankDetails) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.bankDetails = bankDetails;
	}
	public Dealer() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    // Getters and Setters
    
    
}
