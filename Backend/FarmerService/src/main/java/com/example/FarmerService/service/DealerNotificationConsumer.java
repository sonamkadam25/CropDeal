package com.example.FarmerService.service;


import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

import com.example.FarmerService.config.MessagingConfig;
import com.example.FarmerService.dto.CropRequest;


@Component
public class DealerNotificationConsumer {

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void receiveNotification(CropRequest request) {
        System.out.println("Dealer received crop notification: " + request.getName());
    }
}
