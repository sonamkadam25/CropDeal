
package com.example.AdminService.controller;

import com.example.AdminService.config.SecurityConfig;
import com.example.AdminService.dto.*;
import com.example.AdminService.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = AdminController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) 

public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminRequest adminRequest;
    private AdminResponse adminResponse;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setup() {
        adminRequest = new AdminRequest("Admin Name", "admin@example.com", "1234567890", "password123");
        adminResponse = new AdminResponse(1L, "Admin Name", "admin@example.com", "1234567890");

        loginRequest = new LoginRequest("admin@example.com", "password123");
        loginResponse = new LoginResponse("mock-token");
    }

    @Test
    void testRegisterAdmin() throws Exception {
        when(adminService.register(any(AdminRequest.class))).thenReturn(adminResponse);

        ResultActions response = mockMvc.perform(post("/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Name"))
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    void testLoginAdmin() throws Exception {
        when(adminService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        ResultActions response = mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        response.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllFarmers() throws Exception {
        FarmerDto farmer = new FarmerDto(1L, "Farmer One", "farmer1@example.com", true);
        when(adminService.getAllFarmers()).thenReturn(List.of(farmer));

        mockMvc.perform(get("/admin/farmers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateFarmerStatus() throws Exception {
        // Mocking the service method
        Mockito.doNothing().when(adminService).updateFarmerStatus(1L, true);

        mockMvc.perform(put("/admin/farmer/1/status?active=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Farmer status updated successfully."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateFarmerProfile() throws Exception {
        FarmerRequest req = new FarmerRequest("Updated Farmer", "updated@example.com", "9999999999");
        FarmerResponse res = new FarmerResponse(1L, "Updated Farmer", "updated@example.com", true);

        when(adminService.updateFarmerProfile(Mockito.eq(1L), any(FarmerRequest.class))).thenReturn(res);
        
        mockMvc.perform(put("/admin/farmer/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllDealers() throws Exception {
        DealerDto dealer = new DealerDto(1L, "Dealer One", "dealer1@example.com", true);
        when(adminService.getAllDealers()).thenReturn(List.of(dealer));

        mockMvc.perform(get("/admin/dealers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDealerStatus() throws Exception {
        // Mocking the service method
        Mockito.doNothing().when(adminService).updateDealerStatus(1L, false);

        mockMvc.perform(put("/admin/dealer/1/status?active=false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dealer status updated successfully!!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDealerProfile() throws Exception {
        DealerRequest req = new DealerRequest("Updated Dealer", "dealerupdated@example.com", "8888888888");
        DealerResponse res = new DealerResponse(1L, "Updated Dealer", "dealerupdated@example.com", true);

        when(adminService.updateDealerProfile(Mockito.eq(1L), any(DealerRequest.class))).thenReturn(res);

        mockMvc.perform(put("/admin/dealer/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}

