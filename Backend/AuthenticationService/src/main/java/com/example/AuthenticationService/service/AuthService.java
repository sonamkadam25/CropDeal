package com.example.AuthenticationService.service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.AuthenticationService.dto.LoginRequest;
import com.example.AuthenticationService.dto.LoginResponse;
import com.example.AuthenticationService.dto.RegisterRequest;
import com.example.AuthenticationService.entity.User;
import com.example.AuthenticationService.repository.UserRepository;
import com.example.AuthenticationService.security.JwtUtil;
import com.example.AuthenticationService.exception.EmailAlreadyExistsException;
import com.example.AuthenticationService.exception.InvalidCredentialsException;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    
    public void register(RegisterRequest request) {
        if (repo.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered...");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repo.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getRole().name(), user.getEmail());
    }
    
    
    public boolean resetPassword(String email, String newPassword) {
//        Optional<User> userOpt = userRepository.findByEmail(email);
//        if (userOpt.isEmpty()) return false;
    	
    	 System.out.println("🔍 Looking for user by email: " + email);
    	    Optional<User> userOpt = userRepository.findByEmail(email);

    	    if (userOpt.isEmpty()) {
    	        System.out.println("❌ User not found in Auth Service DB");
    	        return false;
    	    }

        User user = userOpt.get();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    
}
