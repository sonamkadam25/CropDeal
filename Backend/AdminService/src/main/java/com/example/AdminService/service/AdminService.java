package com.example.AdminService.service;

import com.example.AdminService.dto.AdminRequest;
import com.example.AdminService.dto.AdminResponse;
import com.example.AdminService.dto.DealerDto;
import com.example.AdminService.dto.DealerRequest;
import com.example.AdminService.dto.DealerResponse;
import com.example.AdminService.dto.FarmerDto;
import com.example.AdminService.dto.FarmerRequest;
import com.example.AdminService.dto.FarmerResponse;
import com.example.AdminService.dto.LoginRequest;
import com.example.AdminService.dto.LoginResponse;
import com.example.AdminService.dto.RegisterRequest;
import com.example.AdminService.entity.Admin;
import com.example.AdminService.enums.Role;
import com.example.AdminService.feign.AuthFeignClient;
import com.example.AdminService.feign.DealerFeignClient;
import com.example.AdminService.feign.FarmerFeignClient;
import com.example.AdminService.repository.AdminRepository;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import com.example.AdminService.entity.Crop;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private FarmerFeignClient farmerFeignClient;

    @Autowired
    private DealerFeignClient dealerFeignClient;

    @Autowired
    private AuthFeignClient authFeignClient;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private HttpServletRequest request;
    
    public AdminResponse register(AdminRequest req) {
    	
        if (adminRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        Admin admin = new Admin();
        admin.setName(req.getName());
        admin.setEmail(req.getEmail());
        admin.setPhone(req.getPhone());
        adminRepository.save(admin);

        RegisterRequest reg = new RegisterRequest();
        reg.setName(req.getName());
        reg.setEmail(req.getEmail());
        reg.setPassword(req.getPassword());
        reg.setRole(Role.ADMIN);
        authFeignClient.register(reg);

        return mapToResponse(admin);
    }

    private AdminResponse mapToResponse(Admin admin) {
    	AdminResponse res = new AdminResponse();
         res.setId(admin.getId());
         res.setName(admin.getName());
         res.setEmail(admin.getEmail());
         res.setPhone(admin.getPhone());
         return res;
	}

    @CircuitBreaker(name = "authService", fallbackMethod = "loginFallback")
    public LoginResponse login(LoginRequest request) {
        try {
            return authFeignClient.login(request);
        } catch (FeignException e) {
            if (e.status() == 403) {
                LoginResponse response = new LoginResponse();
                response.setToken(null); // No token
                response.setRole(null);  // No role
                response.setEmail("Auth service unavailable. Please try again later.");
                return response;
            }
            throw e; 
        }
    }

    public LoginResponse loginFallback(LoginRequest request, Throwable ex) {
        System.err.println("Fallback triggered due to: " + ex.getMessage());

        LoginResponse response = new LoginResponse();
        response.setToken(null); // No token
        response.setRole(null);  // No role
        response.setEmail("Auth service unavailable. Please try again later."); // Use email field to show message if needed

        return response;
    }

    
	public List<FarmerDto> getAllFarmers() {
	    String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }

	    return farmerFeignClient.getAllFarmers(username, role);
	}

    public void updateFarmerStatus(Long id, boolean active) {
    	
    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        farmerFeignClient.updateFarmerStatus(id, active, username, role);
    }
    public FarmerResponse updateFarmerProfile(Long id, FarmerRequest farmerRequest) {

    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        return farmerFeignClient.updateFarmerProfile(id, farmerRequest, username, role);
    }

    public List<DealerDto> getAllDealers() {

    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        return dealerFeignClient.getAllDealers(username, role);
    }

    public void updateDealerStatus(Long id, boolean active) {

    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        dealerFeignClient.updateDealerStatus(id, active,username, role);
    }
    
    public DealerResponse updateDealerProfile(Long id, DealerRequest dealerRequest) {

    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        return dealerFeignClient.updateDealerProfile(id, dealerRequest,username, role);
    }
    
    public List<Crop> getAllCrop() {
    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        return farmerFeignClient.getAllCrops(username, role);
    }
    
    public void deleteCropById(Long cropId) {
        String username = request.getHeader("x-auth-user");
        String role = request.getHeader("x-auth-user-role");

        if (username == null || role == null) {
            throw new RuntimeException("Missing authentication headers");
        }

        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access denied: Only admins can delete crops.");
        }

        farmerFeignClient.deleteCropById(cropId, username, role);
    }

}
