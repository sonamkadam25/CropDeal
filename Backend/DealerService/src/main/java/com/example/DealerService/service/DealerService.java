package com.example.DealerService.service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DealerService.dto.DealerRequest;
import com.example.DealerService.dto.DealerResponse;
import com.example.DealerService.dto.LoginRequest;
import com.example.DealerService.dto.LoginResponse;
import com.example.DealerService.dto.RegisterRequest;
import com.example.DealerService.entity.BankDetails;
import com.example.DealerService.entity.Dealer;
import com.example.DealerService.enums.Role;
import com.example.DealerService.feign.AuthFeignClient;
import com.example.DealerService.feign.CropFeignClient;
import com.example.DealerService.feign.PaymentFeignClient;
import com.example.DealerService.repository.DealerRepository;
import com.example.DealerService.entity.Crop;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class DealerService {
    @Autowired
    DealerRepository dealerRepo;

    @Autowired
    AuthFeignClient authClient;

    @Autowired
    CropFeignClient cropClient;
 
    @Autowired
    PaymentFeignClient paymentClient;
    
    @Autowired
    private HttpServletRequest request;

   //@CircuitBreaker(name = "auth-service", fallbackMethod = "registerFallback")
    public DealerResponse register(DealerRequest req) {
        // Check for existing email
        if (dealerRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        Dealer dealer = new Dealer();
        dealer.setName(req.getName());
        dealer.setEmail(req.getEmail());
        dealer.setPhone(req.getPhone());
        dealerRepo.save(dealer);

        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(req.getEmail());
        reg.setName(req.getName());
        reg.setPassword(req.getPassword());
        reg.setRole(Role.DEALER);
        authClient.register(reg);

        return mapToResponse(dealer);
    }
    public DealerResponse registerFallback(DealerRequest req, Throwable ex) {
        throw new RuntimeException("Auth service is currently unavailable. Please try again later.");
    }

    public LoginResponse login(LoginRequest req) {
        return authClient.login(req);
    }
    
    public List<DealerResponse> getAllDealers() {
        List<Dealer> farmers = dealerRepo.findAll();
        return farmers.stream()
                      .map(this::mapToResponse)
                      .collect(Collectors.toList());
    }
    
    private DealerResponse mapToResponse(Dealer dealer) {
        DealerResponse res = new DealerResponse();
        res.setId(dealer.getId());
        res.setName(dealer.getName() != null ? dealer.getName() : "N/A");
        res.setEmail(dealer.getEmail() != null ? dealer.getEmail() : "N/A");
        res.setPhone(dealer.getPhone() != null ? dealer.getPhone() : "N/A");

        // Fix for potential null value in `active` field
        res.setActive(dealer.isActive() ? dealer.isActive() : false);

        return res;
    }
    
    public boolean updateFarmerStatus(Long id, boolean active) {
        Optional<Dealer> optionalFarmer = dealerRepo.findById(id);
        if (optionalFarmer.isPresent()) {
        	Dealer farmer = optionalFarmer.get();
            farmer.setActive(active);
            dealerRepo.save(farmer);
            return true;
        } else {
            return false;
        }
    }


    public DealerResponse updateProfile(Long id, DealerRequest req) {
        Dealer dealer = dealerRepo.findById(id).orElseThrow();
        dealer.setName(req.getName());
        dealer.setEmail(req.getEmail());
        dealer.setPhone(req.getPhone());
        dealerRepo.save(dealer);
        
        return mapToResponse(dealer);
    }

    public String addBankDetails(Long id, BankDetails details) {
        Dealer dealer = dealerRepo.findById(id).orElseThrow();
        dealer.setBankDetails(details);
        dealerRepo.save(dealer);
        return "Bank details added successfully!!";
    }

    public String subscribeToCrop(Long dealerId, Long cropId, String dealerEmail) {

    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }

         // Forward token to Feign client
         cropClient.subscribeCrop(dealerId, cropId, dealerEmail, username, role);
         return "Dealer Subscribed Successfully to Crop!!";

    }
    
    
    public List<Crop> getAllCrop() {
    	String username = request.getHeader("x-auth-user");
	    String role = request.getHeader("x-auth-user-role");

	    if (username == null || role == null) {
	        throw new RuntimeException("Missing authentication headers");
	    }
        return cropClient.getAllCrops(username, role);
    }

    
    public String getDealerNameById(Long id) {
        Optional<Dealer> dealerOpt = dealerRepo.findById(id);
        if (dealerOpt.isPresent()) {
            return dealerOpt.get().getName();
        } else {
            throw new RuntimeException("Dealer not found with id: " + id);
        }
    }
	public Long getDealerIdByEmail(String email) {
		 Optional<Dealer> dealerOpt = dealerRepo.findByEmail(email);
	        if (dealerOpt.isPresent()) {
	            return dealerOpt.get().getId();
	        } else {
	            throw new RuntimeException("Dealer not found with email: " + email);
	        }
	}
	public Dealer getDealerById(Long id) {
	    Optional<Dealer> dealerOpt = dealerRepo.findById(id);
	    if (dealerOpt.isPresent()) {
	        return dealerOpt.get();
	    } else {
	        throw new RuntimeException("Dealer not found with ID: " + id);
	    }
	}


}