package com.example.AdminService.dto;

public class FarmerDto {
    private Long id;
    private String name;
    private String email;
	private String phone;
    private boolean active;
    
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

    public FarmerDto(Long id, String name, String email, String phone, boolean active) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.active = active;
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public FarmerDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FarmerDto(long l, String string, String string2, boolean b) {
		// TODO Auto-generated constructor stub
	}
    
    
    // getters and setters
}