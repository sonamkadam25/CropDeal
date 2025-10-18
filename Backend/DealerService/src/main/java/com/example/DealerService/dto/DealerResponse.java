package com.example.DealerService.dto;

public class DealerResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    
    private Boolean active = true;

    public boolean isActive() {
        return Boolean.TRUE.equals(active); // avoids NPE
    }
   
	public void setActive(boolean active) {
		this.active = active;
	}
    // Getters and Setters
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
	public DealerResponse(Long id, String name, String email, String phone) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
	}
	public DealerResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
