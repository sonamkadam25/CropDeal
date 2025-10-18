package com.example.FarmerService.service;

import com.example.FarmerService.entity.Crop;
import com.example.FarmerService.entity.Receipt;
import com.example.FarmerService.repository.ReceiptRepository;
import com.example.FarmerService.repository.CropRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReceiptServiceTest {

    @Autowired
    private ReceiptRepository receiptRepo;

    @Mock
    private CropRepository cropRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private Crop crop;

    @BeforeEach
    public void setUp() {
        // Setup mock crop data
        crop = new Crop();
        crop.setId(1L);
        crop.setName("Wheat");
        crop.setType("Cereal");
        crop.setLocation("Farm A");
        crop.setQuantity(100);
    }

    @Test
    public void testCreateReceipt() {
        // Create a new receipt
        Receipt receipt = new Receipt();
        receipt.setCrop(crop);
        receipt.setQuantity(50);
        receipt.setTotalAmount(500.0);
        receipt.setDate(LocalDate.now());

        // Save the receipt using the service
        Receipt savedReceipt = receiptRepo.save(receipt);

        // Verify the saved receipt's details
        assertNotNull(savedReceipt);
        assertEquals(receipt.getQuantity(), savedReceipt.getQuantity());
        assertEquals(receipt.getTotalAmount(), savedReceipt.getTotalAmount());
        assertEquals(receipt.getCrop().getName(), savedReceipt.getCrop().getName());
        assertEquals(receipt.getDate(), savedReceipt.getDate());
    }

    @Test
    public void testGetReceiptById() {
        // Create and save a receipt
        Receipt receipt = new Receipt();
        receipt.setCrop(crop);
        receipt.setQuantity(30);
        receipt.setTotalAmount(300.0);
        receipt.setDate(LocalDate.now());

        receiptRepo.save(receipt);

        // Retrieve the receipt by ID
        Optional<Receipt> foundReceipt = receiptRepo.findById(receipt.getId());

        assertTrue(foundReceipt.isPresent());
        assertEquals(receipt.getId(), foundReceipt.get().getId());
        assertEquals(receipt.getQuantity(), foundReceipt.get().getQuantity());
        assertEquals(receipt.getTotalAmount(), foundReceipt.get().getTotalAmount());
    }
}
