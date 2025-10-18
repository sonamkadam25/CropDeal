package com.example.AdminService.dto;


public class LoginResponse {
    private String token;
    private String role;
    private String email; // ✅ Add this field

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LoginResponse(String token, String role, String email) {
        this.token = token;
        this.role = role;
        this.email  = email;
    }
	public LoginResponse() {
	// TODO Auto-generated constructor stub
}
	public LoginResponse(String token) {
    this.token = token;
}

    // Getters and setters
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

   
}
