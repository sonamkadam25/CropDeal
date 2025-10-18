////package com.example.FarmerService;
////
////import org.junit.jupiter.api.Test;
////import org.springframework.boot.test.context.SpringBootTest;
////
////@SpringBootTest
////class FarmerServiceApplicationTests {
////
////	@Test
////	void contextLoads() {
////	}
////
////}
//
//package com.example.FarmerService;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.example.FarmerService.dto.*;
//import com.example.FarmerService.entity.*;
//import com.example.FarmerService.enums.Role;
//import com.example.FarmerService.feign.AuthFeignClient;
//import com.example.FarmerService.repository.*;
//import com.example.FarmerService.service.FarmerService;
//
//@ExtendWith(MockitoExtension.class)
//class FarmerServiceTest {
//
//    @InjectMocks
//    private FarmerService farmerService;
//
//    @Mock
//    private FarmerRepository farmerRepo;
//
//    @Mock
//    private ReceiptRepository receiptRepo;
//
//    @Mock
//    private AuthFeignClient authFeignClient;
//
//    private Farmer farmer;
//
//    @BeforeEach
//    void setUp() {
//        farmer = new Farmer();
//        farmer.setId(1L);
//        farmer.setName("John Doe");
//        farmer.setEmail("john@example.com");
//        farmer.setPhone("1234567890");
//        farmer.setActive(true);
//    }
//
//    @Test
//    void testRegister() {
//        FarmerRequest request = new FarmerRequest("John Doe", "john@example.com", "1234567890", "password");
//        when(farmerRepo.save(any(Farmer.class))).thenReturn(farmer);
//        doNothing().when(authFeignClient).register(any(RegisterRequest.class));
//
//        FarmerResponse response = farmerService.register(request);
//
//        assertEquals("John Doe", response.getName());
//        verify(farmerRepo).save(any(Farmer.class));
//        verify(authFeignClient).register(any(RegisterRequest.class));
//    }
//
//    @Test
//    void testLogin() {
//        LoginRequest loginRequest = new LoginRequest("john@example.com", "password");
//        LoginResponse loginResponse = new LoginResponse("token", Role.FARMER);
//
//        when(authFeignClient.login(loginRequest)).thenReturn(loginResponse);
//
//        LoginResponse response = farmerService.login(loginRequest);
//        assertEquals("token", response.getToken());
//        assertEquals(Role.FARMER, response.getRole());
//    }
//
//    @Test
//    void testGetAllFarmers() {
//        List<Farmer> farmerList = Arrays.asList(farmer);
//        when(farmerRepo.findAll()).thenReturn(farmerList);
//
//        List<FarmerResponse> responseList = farmerService.getAllFarmers();
//
//        assertEquals(1, responseList.size());
//        assertEquals("John Doe", responseList.get(0).getName());
//    }
//
//    @Test
//    void testUpdateFarmerStatus_FarmerExists() {
//        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));
//
//        boolean result = farmerService.updateFarmerStatus(1L, false);
//        assertTrue(result);
//        verify(farmerRepo).save(any(Farmer.class));
//    }
//
//    @Test
//    void testUpdateFarmerStatus_FarmerNotFound() {
//        when(farmerRepo.findById(2L)).thenReturn(Optional.empty());
//
//        boolean result = farmerService.updateFarmerStatus(2L, false);
//        assertFalse(result);
//    }
//
//    @Test
//    void testUpdateProfile() {
//        FarmerRequest updateRequest = new FarmerRequest("Updated Name", "updated@example.com", "9876543210", "newpass");
//
//        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));
//        when(farmerRepo.save(any(Farmer.class))).thenReturn(farmer);
//
//        FarmerResponse response = farmerService.updateProfile(1L, updateRequest);
//        assertEquals("Updated Name", response.getName());
//    }
//
//    @Test
//    void testAddBankDetails() {
//        BankDetails bankDetails = new BankDetails();
//        bankDetails.setAccountNumber("123456789");
//        bankDetails.setIfsc("IFSC000123");
//
//        when(farmerRepo.findById(1L)).thenReturn(Optional.of(farmer));
//        farmerService.addBankDetails(1L, bankDetails);
//        verify(farmerRepo).save(any(Farmer.class));
//    }
//
//    @Test
//    void testGetReceipts() {
//        Crop crop = new Crop();
//        crop.setName("Wheat");
//
//        Receipt receipt = new Receipt();
//        receipt.setId(1L);
//        receipt.setCrop(crop);
//        receipt.setQuantity(10.0);
//        receipt.setTotalAmount(500.0);
//        receipt.setDate(LocalDate.now());
//
//        when(receiptRepo.findByCropFarmerId(1L)).thenReturn(Arrays.asList(receipt));
//
//        List<ReceiptResponse> responses = farmerService.getReceipts(1L);
//        assertEquals(1, responses.size());
//        assertEquals("Wheat", responses.get(0).getCropName());
//    }
//}
