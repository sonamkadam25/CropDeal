//package com.example.DealerService.controller;
//
//import com.example.DealerService.dto.*;
//import com.example.DealerService.entity.BankDetails;
//import com.example.DealerService.service.DealerService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(DealerController.class)
//@AutoConfigureMockMvc(addFilters = false) // ✅ disables Spring Security
//public class DealerControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private DealerService service;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testRegister() throws Exception {
//        DealerRequest request = new DealerRequest();
//        DealerResponse response = new DealerResponse();
//        when(service.register(any(DealerRequest.class))).thenReturn(response);
//
//        mockMvc.perform(post("/dealer/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testLogin() throws Exception {
//        LoginRequest request = new LoginRequest();
//        LoginResponse response = new LoginResponse();
//        when(service.login(any(LoginRequest.class))).thenReturn(response);
//
//        mockMvc.perform(post("/dealer/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetAllDealers() throws Exception {
//        when(service.getAllDealers()).thenReturn(List.of());
//
//        mockMvc.perform(get("/dealer/all"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testUpdateFarmerStatus() throws Exception {
//        when(service.updateFarmerStatus(1L, true)).thenReturn(true);
//
//        mockMvc.perform(put("/dealer/1/status")
//                .param("active", "true"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Dealer status updated successfully."));
//    }
//
//    @Test
//    public void testAddBank() throws Exception {
//        BankDetails bank = new BankDetails();
//        when(service.addBankDetails(eq(1L), any(BankDetails.class))).thenReturn("Bank details added.");
//
//        mockMvc.perform(post("/dealer/bank/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(bank)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Bank details added."));
//    }
//
//    @Test
//    public void testSubscribeCrop() throws Exception {
//        when(service.subscribeToCrop(2L, 10L)).thenReturn("Subscribed successfully.");
//
//        mockMvc.perform(post("/dealer/subscribe")
//                .param("dealerId", "2")
//                .param("cropId", "10"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Subscribed successfully."));
//    }
//
//    @Test
//    public void testUpdateProfile() throws Exception {
//        DealerRequest request = new DealerRequest();
//        DealerResponse response = new DealerResponse();
//        when(service.updateProfile(eq(5L), any(DealerRequest.class))).thenReturn(response);
//
//        mockMvc.perform(put("/dealer/update/5")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetDealerName() throws Exception {
//        when(service.getDealerNameById(3L)).thenReturn("Jane Dealer");
//
//        mockMvc.perform(get("/dealer/name/3"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Jane Dealer"));
//    }
//
//    @Test
//    public void testGetDealerName_NotFound() throws Exception {
//        when(service.getDealerNameById(9L)).thenThrow(new RuntimeException("Dealer not found"));
//
//        mockMvc.perform(get("/dealer/name/9"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("Dealer not found"));
//    }
//}
