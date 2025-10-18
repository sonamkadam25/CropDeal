package com.example.FarmerService.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.FarmerService.config.MessagingConfig;
import com.example.FarmerService.dto.CropPostResponse;
import com.example.FarmerService.dto.CropRequest;
import com.example.FarmerService.entity.Crop;
import com.example.FarmerService.entity.DealerCropSubscription;
import com.example.FarmerService.entity.Farmer;
import com.example.FarmerService.repository.CropRepository;
import com.example.FarmerService.repository.DealerCropSubscriptionRepository;
import com.example.FarmerService.repository.FarmerRepository;


@Service
public class CropService {
    @Autowired
    private CropRepository cropRepo;

    @Autowired
    private FarmerRepository farmerRepo;
    
    @Autowired
    private DealerCropSubscriptionRepository subscriptionRepo;
    
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private EmailSenderService emailSenderService; // Add this line

    
    
    public CropPostResponse postCropAndNotifyDealers(Long farmerId, CropRequest req) {
        Farmer farmer = farmerRepo.findById(farmerId).orElseThrow();

        Crop crop = new Crop();
        crop.setFarmer(farmer);
        crop.setName(req.getName());
        crop.setType(req.getType());
        crop.setLocation(req.getLocation());
        crop.setQuantity(req.getQuantity());
        crop.setPricePerUnit(req.getPricePerUnit());

        Crop savedCrop = cropRepo.save(crop);

        List<DealerCropSubscription> subscribers =subscriptionRepo.findByCrop_Type(req.getType());



        Set<Long> notifiedDealerIds = new HashSet<>();
        Set<String> notifiedDealerEmails = new HashSet<>();


        for (DealerCropSubscription subscription : subscribers) {
            Long dealerId = subscription.getDealerId();
            String dealerEmail = subscription.getEmail();
            
            notifiedDealerIds.add(dealerId);
            notifiedDealerEmails.add(dealerEmail);

            System.out.println("Notifying dealer " + dealerId + " about new crop: " + savedCrop.getName());
            System.out.println("Notifying dealer email " + dealerEmail + " about new crop: " + savedCrop.getName());

            
            if (dealerEmail != null && !dealerEmail.isEmpty()) {
                String subject = "New Crop Available: " + savedCrop.getName();
                String body = "Hello Dealer,\n\nA new crop has been posted by a farmer:\n"
                            + "Crop Name: " + savedCrop.getName() + "\n"
                            + "Type: " + savedCrop.getType() + "\n"
                            + "Location: " + savedCrop.getLocation() + "\n"
                            + "Quantity: " + savedCrop.getQuantity() + " kg\n\n"
                            + "Thank you.";

                emailSenderService.sendEmail(dealerEmail, subject, body);
            } else {
                System.err.println("Skipping email: Dealer email is null for dealer ID " + dealerId);
            }
            
            rabbitTemplate.convertAndSend(
                MessagingConfig.EXCHANGE,
                MessagingConfig.ROUTING_KEY,
                req
            );
        }

        return new CropPostResponse(savedCrop, new ArrayList<>(notifiedDealerIds));
    }

    
    public void subscribeCrop(Long dealerId, Long cropId, String dealerEmail) {
        Crop crop = cropRepo.findById(cropId).orElseThrow();

        DealerCropSubscription subscription = new DealerCropSubscription();
        subscription.setDealerId(dealerId);
        subscription.setEmail(dealerEmail);
        subscription.setCrop(crop);

        subscriptionRepo.save(subscription);
    }


    public List<Crop> getCropsByFarmerId(Long farmerId) {
        return cropRepo.findByFarmerId(farmerId);
    }
    
    
    public List<Crop> getAllCrops() {
        return cropRepo.findAll();
    }
    
    public List<Crop> getAllCropForHomePage()  {
        return cropRepo.findAll();
    }
      
    public void deleteCropByAdmin(Long cropId) {
        Crop crop = cropRepo.findById(cropId)
            .orElseThrow(() -> new RuntimeException("Crop not found with ID: " + cropId));

        // Optional: clean up dealer subscriptions related to this crop
        subscriptionRepo.deleteByCrop(crop);

        cropRepo.delete(crop);
    }


}
