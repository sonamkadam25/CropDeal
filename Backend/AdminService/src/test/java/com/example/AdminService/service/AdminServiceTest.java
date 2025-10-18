package com.example.AdminService.service;

import com.example.AdminService.dto.*;
import com.example.AdminService.entity.Admin;
import com.example.AdminService.feign.AuthFeignClient;
import com.example.AdminService.feign.DealerFeignClient;
import com.example.AdminService.feign.FarmerFeignClient;
import com.example.AdminService.repository.AdminRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private FarmerFeignClient farmerFeignClient;

    @Mock
    private DealerFeignClient dealerFeignClient;

    @Mock
    private AuthFeignClient authFeignClient;

    @Mock
    private HttpServletRequest request;

    private AdminRequest adminRequest;
    private Admin admin;
    
    @BeforeEach
    void setup() {
        adminRequest = new AdminRequest("Admin Name", "admin@example.com", "1234567890", "password123");

        admin = new Admin();
        admin.setId(1L);
        admin.setName("Admin Name");
        admin.setEmail("admin@example.com");
        admin.setPhone("1234567890");

        when(request.getHeader("x-auth-user")).thenReturn("admin@example.com");
        when(request.getHeader("x-auth-user-role")).thenReturn("ADMIN");
    }

    @Test
    void testGetAllDealers() {
        DealerDto dealer = new DealerDto(1L, "Dealer One", "dealer1@example.com", "9999999999", true);
        when(dealerFeignClient.getAllDealers("admin@example.com", "ADMIN")).thenReturn(List.of(dealer));

        List<DealerDto> dealers = adminService.getAllDealers();

        assertNotNull(dealers);
        assertEquals(1, dealers.size());
        assertEquals("Dealer One", dealers.get(0).getName());
    }

    @Test
    void testGetAllFarmers() {
        FarmerDto farmer = new FarmerDto(1L, "Farmer One", "farmer1@example.com", "9999999999", true);
        when(farmerFeignClient.getAllFarmers("admin@example.com", "ADMIN")).thenReturn(List.of(farmer));

        List<FarmerDto> farmers = adminService.getAllFarmers();

        assertNotNull(farmers);
        assertEquals(1, farmers.size());
        assertEquals("Farmer One", farmers.get(0).getName());
    }

    @Test
    void testUpdateFarmerStatus() {
        doNothing().when(farmerFeignClient).updateFarmerStatus(1L, true, "admin@example.com", "ADMIN");

        adminService.updateFarmerStatus(1L, true);

        verify(farmerFeignClient, times(1)).updateFarmerStatus(1L, true, "admin@example.com", "ADMIN");
    }
    
    @Test
    void testUpdateFarmerProfile() {
   	FarmerRequest req = new FarmerRequest("Updated Farmer", "updated@example.com", "9999999999", "123");
        FarmerResponse expectedResponse = new FarmerResponse(1L, "Updated Farmer", "updated@example.com", "9999999999" , true);

        when(farmerFeignClient.updateFarmerProfile(
                eq(1L),
                eq(req),
                eq("admin@example.com"),
                eq("ADMIN")
        )).thenReturn(expectedResponse);
        
        FarmerResponse actualResponse = adminService.updateFarmerProfile(1L, req);
        assertEquals("Updated Farmer", actualResponse.getName());
        assertEquals("updated@example.com", actualResponse.getEmail());
    }
  
    @Test
    void testUpdateDealerProfile() {
        DealerRequest req = new DealerRequest("Updated Dealer", "dealerupdated@example.com", "8888888888","123");
        DealerResponse res = new DealerResponse(1L, "Updated Dealer", "dealerupdated@example.com","8888888888" , true);
        when(dealerFeignClient.updateDealerProfile(
        		eq(1L),
                eq(req),
                eq("admin@example.com"),
                eq("ADMIN")
           )).thenReturn(res);

        DealerResponse response = adminService.updateDealerProfile(1L, req);

        assertEquals("Updated Dealer", response.getName());
        assertEquals("dealerupdated@example.com", response.getEmail());
    }
    
    @Test
    void testUpdateDealerStatus() {
        doNothing().when(dealerFeignClient).updateDealerStatus(1L, false, "admin@example.com", "ADMIN");

        adminService.updateDealerStatus(1L, false);

        verify(dealerFeignClient, times(1)).updateDealerStatus(1L, false, "admin@example.com", "ADMIN");
    }


}

