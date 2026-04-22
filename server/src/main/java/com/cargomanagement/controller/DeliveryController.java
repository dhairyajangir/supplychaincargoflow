package com.cargomanagement.controller;

import com.cargomanagement.models.Delivery;
import com.cargomanagement.repository.DeliveryRepository;
import com.cargomanagement.service.KafkaProducerService;
import com.cargomanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final KafkaProducerService kafkaProducerService;
    private final NotificationService notificationService;

    @Autowired
    public DeliveryController(DeliveryRepository deliveryRepository,
                              KafkaProducerService kafkaProducerService,
                              NotificationService notificationService) {
        this.deliveryRepository = deliveryRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        // Only return deliveries where the associated shipment is actually "Delivered"
        return deliveryRepository.findAll().stream()
                .filter(delivery -> delivery.getShipment() != null && 
                        "Delivered".equals(delivery.getShipment().getStatus()))
                .collect(Collectors.toList());
    }

    @GetMapping("/cleanup")
    public ResponseEntity<String> cleanupInconsistentDeliveries() {
        // Find and remove deliveries where shipment status is not "Delivered"
        List<Delivery> inconsistentDeliveries = deliveryRepository.findAll().stream()
                .filter(delivery -> delivery.getShipment() != null && 
                        !"Delivered".equals(delivery.getShipment().getStatus()))
                .collect(Collectors.toList());
        
        int deletedCount = inconsistentDeliveries.size();
        deliveryRepository.deleteAll(inconsistentDeliveries);
        
        return ResponseEntity.ok("Cleaned up " + deletedCount + " inconsistent delivery records");
    }

    @PostMapping
    public Delivery createDelivery(@RequestBody Delivery delivery) {
        Delivery savedDelivery = deliveryRepository.save(delivery);
        String message = "Delivery created: ID=" + savedDelivery.getDeliveryId() + ", Recipient=" + savedDelivery.getRecipient();
        kafkaProducerService.sendMessage("delivery-events", message);
        notificationService.notifyDeliveryEvent(savedDelivery, "Created");
        return savedDelivery;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
        return ResponseEntity.ok(delivery);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable Long id, @RequestBody Delivery deliveryDetails) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        delivery.setShipment(deliveryDetails.getShipment());
        delivery.setActualDeliveryDate(deliveryDetails.getActualDeliveryDate());
        delivery.setRecipient(deliveryDetails.getRecipient());

        final Delivery updatedDelivery = deliveryRepository.save(delivery);
        String message = "Delivery updated: ID=" + id + ", Recipient=" + updatedDelivery.getRecipient();
        kafkaProducerService.sendMessage("delivery-events", message);
        notificationService.notifyDeliveryEvent(updatedDelivery, "Updated");
        return ResponseEntity.ok(updatedDelivery);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDelivery(@PathVariable Long id) {
        try {
            Delivery delivery = deliveryRepository.findById(id).orElse(null);
            if (delivery == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Delivery not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            deliveryRepository.delete(delivery);

            // Publish to Kafka
            String message = "Delivery deleted: ID=" + id;
            kafkaProducerService.sendMessage("delivery-events", message);
            notificationService.notifyDeliveryEvent(delivery, "Deleted");

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery deleted successfully");
            response.put("deliveryId", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting delivery: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
