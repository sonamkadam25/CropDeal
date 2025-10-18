//package com.example.FarmerService.controller;
//
//import com.example.FarmerService.dto.CropRequest;
//import com.example.FarmerService.entity.Crop;
//import com.example.FarmerService.service.CropService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CropController.class)
//@AutoConfigureMockMvc(addFilters = false)
//public class CropControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CropService cropService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
////    @Test
////    @WithMockUser(roles = "FARMER")
////    public void testPostCrop() throws Exception {
////        CropRequest request = new CropRequest();
////        Crop crop = new Crop();
////        when(cropService.postCropAndNotifyDealers(eq(1L), any(CropRequest.class))).thenReturn(crop);
////
////        mockMvc.perform(post("/farmers/crops/1/post")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(request)))
////                .andExpect(status().isOk());
////    }
//
//    @Test
//    public void testGetFarmerCrops() throws Exception {
//        Crop crop = new Crop();
//        when(cropService.getCropsByFarmerId(1L)).thenReturn(List.of(crop));
//
//        mockMvc.perform(get("/farmers/crops/1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(roles = "DEALER")
//    public void testSubscribeToCrop() throws Exception {
//        doNothing().when(cropService).subscribeCrop(2L, 5L);
//
//        mockMvc.perform(post("/farmers/crops/subscribe")
//                .param("dealerId", "2")
//                .param("cropId", "5"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Subscribed successfully to crop ID: 5"));
//    }
//}
