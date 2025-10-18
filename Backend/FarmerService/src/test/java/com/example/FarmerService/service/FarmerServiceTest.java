package com.example.FarmerService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.FarmerService.dto.*;
import com.example.FarmerService.entity.BankDetails;
import com.example.FarmerService.entity.Farmer;
import com.example.FarmerService.entity.Receipt;
import com.example.FarmerService.enums.Role;
import com.example.FarmerService.feign.AuthFeignClient;
import com.example.FarmerService.repository.FarmerRepository;
import com.example.FarmerService.repository.ReceiptRepository;

class FarmerServiceTest {

    @Mock
    private FarmerRepository farmerRepo;

    @Mock
    private ReceiptRepository receiptRepo;

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private FarmerService farmerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        FarmerRequest request = new FarmerRequest("John", "john@example.com", "1234567890", "password");
        Farmer farmer = new Farmer();
        farmer.setName("John");
        farmer.setEmail("john@example.com");
        farmer.setPhone("1234567890");

        when(farmerRepo.save(any(Farmer.class))).thenReturn(farmer);

        FarmerResponse response = farmerService.register(request);

        assertEquals("John", response.getName());
        verify(authFeignClient).register(any(RegisterRequest.class));
    }

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest("john@example.com", "password");
        LoginResponse expectedResponse = new LoginResponse("token123", Role.FARMER.name(), null);
        when(authFeignClient.login(request)).thenReturn(expectedResponse);

        LoginResponse response = farmerService.login(request);
        assertEquals("token123", response.getToken());
    }

    @Test
    void testGetAllFarmers() {
        Farmer f1 = new Farmer(1L, "John", "john@example.com", "1234567890", null);
        Farmer f2 = new Farmer(2L, "Jane", "jane@example.com", "0987654321", null);
        when(farmerRepo.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FarmerResponse> farmers = farmerService.getAllFarmers();
        assertEquals(2, farmers.size());
    }

    @Test
    void testUpdateFarmerStatus() {
        Farmer farmer = new Farmer();
        farmer.setId(1L);
        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));

        boolean result = farmerService.updateFarmerStatus(1L, false);
        assertTrue(result);
        verify(farmerRepo).save(farmer);
    }

    @Test
    void testUpdateProfile() {
        Farmer farmer = new Farmer(1L, "John", "john@example.com", "1234567890", null);
        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));

        FarmerRequest req = new FarmerRequest("Jane", "jane@example.com", "1112223333", "");
        FarmerResponse res = farmerService.updateProfile(1L, req);
        assertEquals("Jane", res.getName());
        verify(farmerRepo).save(any(Farmer.class));
    }

    @Test
    void testAddBankDetails() {
        Farmer farmer = new Farmer();
        BankDetails bankDetails = new BankDetails();
        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));

        farmerService.addBankDetails(1L, bankDetails);
        verify(farmerRepo).save(farmer);
    }

    @Test
    void testGetReceipts() {
        Receipt receipt = new Receipt();
        receipt.setId(1L);
        receipt.setQuantity(5);
        receipt.setTotalAmount((double) 1000);
        receipt.setDate("2024-04-20");

        com.example.FarmerService.entity.Crop crop = new com.example.FarmerService.entity.Crop();
        crop.setName("Wheat");
        receipt.setCrop(crop);

        when(receiptRepo.findByCropFarmerId(1L)).thenReturn(List.of(receipt));

        List<ReceiptResponse> responses = farmerService.getReceipts(1L);
        assertEquals(1, responses.size());
        assertEquals("Wheat", responses.get(0).getCropName());
    }

    @Test
    void testGetFarmerNameById() {
        Farmer farmer = new Farmer();
        farmer.setId(1L);
        farmer.setName("John");
        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));

        String name = farmerService.getFarmerNameById(1L);
        assertEquals("John", name);
    }
}
