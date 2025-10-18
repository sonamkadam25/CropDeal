package com.example.FarmerService.service;

import com.example.FarmerService.entity.BankDetails;
import com.example.FarmerService.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private BankRepository bankRepo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveBankDetails() {
        BankDetails details = new BankDetails(1L, "1234567890", "SBIN0000123", "SBI");

        when(bankRepo.save(any(BankDetails.class))).thenReturn(details);

        BankDetails saved = bankService.save(details);

        assertNotNull(saved);
        assertEquals("1234567890", saved.getAccountNumber());
        assertEquals("SBIN0000123", saved.getIfsc());
        assertEquals("SBI", saved.getBankName());
    }
}
