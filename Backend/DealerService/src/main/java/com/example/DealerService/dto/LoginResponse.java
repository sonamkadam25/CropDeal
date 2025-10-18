package com.example.DealerService.dto;

public class LoginResponse {
    private String token;
    private String role;
    // Getters and Setters
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public LoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LoginResponse(String string, String string2) {
		// TODO Auto-generated constructor stub
	}
    
    
}