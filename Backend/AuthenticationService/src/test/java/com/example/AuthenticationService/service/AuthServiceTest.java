package com.example.AuthenticationService.service;

import com.example.AuthenticationService.dto.LoginRequest;
import com.example.AuthenticationService.dto.LoginResponse;
import com.example.AuthenticationService.dto.RegisterRequest;
import com.example.AuthenticationService.entity.User;
import com.example.AuthenticationService.enums.Role;
import com.example.AuthenticationService.repository.UserRepository;
import com.example.AuthenticationService.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test for successful registration
    @Test
    void register_ShouldSaveUser_WhenEmailNotExists() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123", Role.USER);

        when(repo.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("encodedPassword");

        authService.register(request);

        verify(repo, times(1)).save(any(User.class));
    }

    // ✅ Test for successful login
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("john@example.com", "password123");
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(repo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mocked-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("USER", response.getRole());
    }

    // ❌ Test for login failure due to user not found
    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        when(repo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("User not found", exception.getMessage());
    }

    // ❌ Test for login failure due to wrong password
    @Test
    void login_ShouldThrowException_WhenPasswordInvalid() {
        LoginRequest request = new LoginRequest("john@example.com", "wrongPassword");
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(repo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
