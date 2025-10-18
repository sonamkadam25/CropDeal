//package com.example.FarmerService.service;
//
//import com.example.FarmerService.dto.CropRequest;
//import com.example.FarmerService.entity.Crop;
//import com.example.FarmerService.entity.DealerCropSubscription;
//import com.example.FarmerService.entity.Farmer;
//import com.example.FarmerService.repository.CropRepository;
//import com.example.FarmerService.repository.DealerCropSubscriptionRepository;
//import com.example.FarmerService.repository.FarmerRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class CropServiceTest {
//
//    @InjectMocks
//    private CropService cropService;
//
//    @Mock
//    private CropRepository cropRepo;
//
//    @Mock
//    private FarmerRepository farmerRepo;
//
//    @Mock
//    private DealerCropSubscriptionRepository subscriptionRepo;
//
//    @Mock
//    private RabbitTemplate rabbitTemplate;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testPostCropAndNotifyDealers() {
//        Long farmerId = 1L;
//        Farmer farmer = new Farmer();
//        farmer.setId(farmerId);
//
//        CropRequest request = new CropRequest();
//        request.setName("Wheat");
//        request.setType("Grain");
//        request.setLocation("Pune");
//        request.setQuantity(100);
//
//        Crop savedCrop = new Crop();
//        savedCrop.setId(1L);
//        savedCrop.setName("Wheat");
//
//        DealerCropSubscription subscription = new DealerCropSubscription();
//        subscription.setDealerId(2L);
//        subscription.setCrop(savedCrop);
//
//        when(farmerRepo.findById(farmerId)).thenReturn(Optional.of(farmer));
//        when(cropRepo.save(any(Crop.class))).thenReturn(savedCrop);
//        when(subscriptionRepo.findByCrop_Type("Grain")).thenReturn(List.of(subscription));
//
////        Crop result = cropService.postCropAndNotifyDealers(farmerId, request);
//
////        assertNotNull(result);
////        assertEquals("Wheat", result.getName());
//        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), eq(request));
//    }
//
//    @Test
//    public void testSubscribeCrop() {
//        Long dealerId = 1L;
//        Long cropId = 2L;
//
//        Crop crop = new Crop();
//        crop.setId(cropId);
//
//        when(cropRepo.findById(cropId)).thenReturn(Optional.of(crop));
//
//        cropService.subscribeCrop(dealerId, cropId);
//
//        verify(subscriptionRepo, times(1)).save(any(DealerCropSubscription.class));
//    }
//
//    @Test
//    public void testGetCropsByFarmerId() {
//        Long farmerId = 1L;
//        Crop crop = new Crop();
//        crop.setName("Rice");
//
//        when(cropRepo.findByFarmerId(farmerId)).thenReturn(List.of(crop));
//
//        List<Crop> crops = cropService.getCropsByFarmerId(farmerId);
//
//        assertEquals(1, crops.size());
//        assertEquals("Rice", crops.get(0).getName());
//    }
//}
