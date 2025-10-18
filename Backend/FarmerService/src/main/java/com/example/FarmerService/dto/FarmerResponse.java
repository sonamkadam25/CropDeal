package com.example.FarmerService.dto;

import lombok.Data;

@Data
public class FarmerResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean active = true;
    
    private String message;

    public FarmerResponse(String message) {
        this.message = message;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active); // avoids NPE
    }
   
	public void setActive(boolean active) {
		this.active = active;
	}
	
    
//    public void setActive(boolean active) {
//        this.active = active;
//    }
    
	public FarmerResponse(Long id, String name, String email, String phone, boolean active) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.active = active;
	}


	public boolean getActive() {
		return active;
	}

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
	public FarmerResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}

