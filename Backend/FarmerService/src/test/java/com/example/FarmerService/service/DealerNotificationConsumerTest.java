package com.example.FarmerService.service;

import com.example.FarmerService.dto.CropRequest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class DealerNotificationConsumerTest {

    @Test
    public void testReceiveNotification() {
        // Arrange
        DealerNotificationConsumer consumer = new DealerNotificationConsumer();
        CropRequest request = new CropRequest();
        request.setName("Maize");

        // Capture System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Act
        consumer.receiveNotification(request);

        // Assert
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Dealer received crop notification: Maize"));

        // Reset System.out
        System.setOut(System.out);
    }
}
