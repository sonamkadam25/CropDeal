package com.example.DealerService.service;

import com.example.DealerService.dto.*;
import com.example.DealerService.entity.BankDetails;
import com.example.DealerService.entity.Dealer;
import com.example.DealerService.feign.AuthFeignClient;
import com.example.DealerService.feign.CropFeignClient;
import com.example.DealerService.feign.PaymentFeignClient;
import com.example.DealerService.repository.DealerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealerServiceTest {

    @Mock
    private DealerRepository dealerRepo;

    @Mock
    private AuthFeignClient authClient;

    @Mock
    private CropFeignClient cropClient;

    @Mock
    private PaymentFeignClient paymentClient;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private DealerService dealerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        DealerRequest req = new DealerRequest("John", "john@example.com", "1234567890", "password");
        Dealer savedDealer = new Dealer();
        savedDealer.setId(1L);
        savedDealer.setName("John");
        savedDealer.setEmail("john@example.com");
        savedDealer.setPhone("1234567890");

        when(dealerRepo.save(any(Dealer.class))).thenReturn(savedDealer);

        DealerResponse response = dealerService.register(req);

        assertNotNull(response);
        assertEquals("John", response.getName());
    }
    
    @Test
    void testLogin() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        LoginResponse expectedResponse = new LoginResponse("token", "DEALER");

        when(authClient.login(loginRequest)).thenReturn(expectedResponse);

        LoginResponse actualResponse = dealerService.login(loginRequest);
        assertEquals(expectedResponse.getToken(), actualResponse.getToken());
    }

    @Test
    void testGetAllDealers() {
        Dealer dealer = new Dealer(1L, "Alice", "alice@example.com", "1111111111", null);
        when(dealerRepo.findAll()).thenReturn(Arrays.asList(dealer));

        assertEquals(1, dealerService.getAllDealers().size());
    }

    @Test
    void testUpdateFarmerStatus() {
        Dealer dealer = new Dealer();
        dealer.setActive(true);
        when(dealerRepo.findById(1L)).thenReturn(Optional.of(dealer));

        boolean result = dealerService.updateFarmerStatus(1L, false);
        assertTrue(result);
        assertFalse(dealer.isActive());
    }

    @Test
    void testUpdateProfile() {
        Dealer dealer = new Dealer(1L, "John", "john@abc.com", "9876543210", null);
        DealerRequest req = new DealerRequest("Johnny", "johnny@abc.com", "0001112222", "");

        when(dealerRepo.findById(1L)).thenReturn(Optional.of(dealer));

        DealerResponse updated = dealerService.updateProfile(1L, req);
        assertEquals("Johnny", updated.getName());
    }

    @Test
    void testAddBankDetails() {
        Dealer dealer = new Dealer();
        when(dealerRepo.findById(1L)).thenReturn(Optional.of(dealer));

        BankDetails details = new BankDetails();
        String msg = dealerService.addBankDetails(1L, details);

        assertEquals("Bank details added successfully!!", msg);
    }

    @Test
    void testGetDealerNameById() {
        Dealer dealer = new Dealer();
        dealer.setName("Alex");
        when(dealerRepo.findById(1L)).thenReturn(Optional.of(dealer));

        String name = dealerService.getDealerNameById(1L);
        assertEquals("Alex", name);
    }

    @Test
    void testGetDealerNameById_NotFound() {
        when(dealerRepo.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> dealerService.getDealerNameById(2L));
        assertEquals("Dealer not found with id: 2", exception.getMessage());
    }
}
