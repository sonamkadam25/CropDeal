package com.example.FarmerService.controller;

import com.example.FarmerService.dto.*;
import com.example.FarmerService.entity.BankDetails;
import com.example.FarmerService.service.FarmerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FarmerController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class FarmerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FarmerService farmerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister() throws Exception {
        FarmerRequest request = new FarmerRequest();
        FarmerResponse response = new FarmerResponse();

        when(farmerService.register(any(FarmerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/farmers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        LoginResponse loginResponse = new LoginResponse();

        when(farmerService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/farmers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllFarmers() throws Exception {
        when(farmerService.getAllFarmers()).thenReturn(List.of());

        mockMvc.perform(get("/farmers/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateFarmerStatus() throws Exception {
        when(farmerService.updateFarmerStatus(1L, true)).thenReturn(true);

        mockMvc.perform(put("/farmers/1/status?active=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Farmer status updated successfully."));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        FarmerRequest request = new FarmerRequest();
        FarmerResponse response = new FarmerResponse();

        when(farmerService.updateProfile(eq(1L), any(FarmerRequest.class))).thenReturn(response);

        mockMvc.perform(put("/farmers/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddBankDetails() throws Exception {
        BankDetails bankDetails = new BankDetails();

        doNothing().when(farmerService).addBankDetails(eq(1L), any(BankDetails.class));

        mockMvc.perform(post("/farmers/1/bank")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Bank details added successfully."));
    }

    @Test
    public void testGetFarmerName() throws Exception {
        when(farmerService.getFarmerNameById(1L)).thenReturn("John Doe");

        mockMvc.perform(get("/farmers/name/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("John Doe"));
    }
}
