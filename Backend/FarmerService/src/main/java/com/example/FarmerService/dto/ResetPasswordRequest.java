package com.example.FarmerService.dto;

public class ResetPasswordRequest {
    private String email;
    private String newPassword;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public ResetPasswordRequest(String email, String newPassword) {
		super();
		this.email = email;
		this.newPassword = newPassword;
	}
	public ResetPasswordRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}