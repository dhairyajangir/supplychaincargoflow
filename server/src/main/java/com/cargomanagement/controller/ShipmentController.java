package com.cargomanagement.controller;

import com.cargomanagement.models.Shipment;
import com.cargomanagement.models.Delivery;
import com.cargomanagement.repository.ShipmentRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final DeliveryRepository deliveryRepository;
    private final KafkaProducerService kafkaProducerService;
    private final NotificationService notificationService;

    @Autowired
    public ShipmentController(ShipmentRepository shipmentRepository,
                            DeliveryRepository deliveryRepository,
                            KafkaProducerService kafkaProducerService,
                            NotificationService notificationService) {
        this.shipmentRepository = shipmentRepository;
        this.deliveryRepository = deliveryRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        try {
            List<Shipment> shipments = shipmentRepository.findAllWithDetails(); // Use the new method here
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            System.err.println("Error fetching shipments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        try {
            Optional<Shipment> shipment = shipmentRepository.findById(id);
            if (shipment.isPresent()) {
                return ResponseEntity.ok(shipment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching shipment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        try {
            System.out.println("Received shipment: " + shipment);
            Shipment savedShipment = shipmentRepository.save(shipment);
            
            // Publish Kafka event
            String message = "Shipment created: ID=" + savedShipment.getShipmentId() + 
                           ", Origin=" + savedShipment.getOrigin() + 
                           ", Destination=" + savedShipment.getDestination();
            kafkaProducerService.sendMessage("shipment-events", message);
            notificationService.notifyShipmentCreated(savedShipment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedShipment);
        } catch (Exception e) {
            System.err.println("Error creating shipment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Long id, @RequestBody Shipment shipmentDetails) {
        try {
            Optional<Shipment> existingShipment = shipmentRepository.findById(id);
            if (existingShipment.isPresent()) {
                Shipment shipment = existingShipment.get();
                String oldStatus = shipment.getStatus(); // Store old status
                
                shipment.setOrigin(shipmentDetails.getOrigin());
                shipment.setDestination(shipmentDetails.getDestination());
                shipment.setStatus(shipmentDetails.getStatus());
                shipment.setEstimatedDelivery(shipmentDetails.getEstimatedDelivery());
                
                // Set route and vendor if provided
                if (shipmentDetails.getAssignedRoute() != null) {
                    shipment.setAssignedRoute(shipmentDetails.getAssignedRoute());
                }
                if (shipmentDetails.getAssignedVendor() != null) {
                    shipment.setAssignedVendor(shipmentDetails.getAssignedVendor());
                }
                
                Shipment updatedShipment = shipmentRepository.save(shipment);

                // Automatically create delivery record when shipment status changes to "Delivered"
                if ("Delivered".equals(shipmentDetails.getStatus()) && 
                    !"Delivered".equals(oldStatus)) {
                    
                    // Check if delivery record already exists for this shipment
                    boolean deliveryExists = deliveryRepository.findAll().stream()
                        .anyMatch(d -> d.getShipment() != null && 
                                      d.getShipment().getShipmentId().equals(id));
                    
                    if (!deliveryExists) {
                        Delivery delivery = new Delivery();
                        delivery.setShipment(updatedShipment);
                        // Convert LocalDate to LocalDateTime (start of day)
                        delivery.setActualDeliveryDate(updatedShipment.getEstimatedDelivery().atStartOfDay());
                        delivery.setRecipient("Customer at " + updatedShipment.getDestination());
                        deliveryRepository.save(delivery);
                    }
                }

                // Note: For now, shipment updates work normally
                // Delivery synchronization is handled by the improved DeliveryController.getAllDeliveries() method
                // which filters deliveries to only show those with "Delivered" status

                // Publish Kafka event
                String message = "Shipment updated: ID=" + id + ", Status=" + updatedShipment.getStatus();
                kafkaProducerService.sendMessage("shipment-events", message);
                notificationService.notifyShipmentUpdated(updatedShipment, oldStatus);
                
                return ResponseEntity.ok(updatedShipment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error updating shipment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteShipment(@PathVariable Long id) {
        try {
            Shipment shipment = shipmentRepository.findById(id).orElse(null);
            if (shipment == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Shipment not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Delete associated deliveries first (to avoid foreign key constraint violation)
            List<Delivery> associatedDeliveries = deliveryRepository.findAll().stream()
                .filter(d -> d.getShipment() != null && d.getShipment().getShipmentId().equals(id))
                .toList();
            
            if (!associatedDeliveries.isEmpty()) {
                deliveryRepository.deleteAll(associatedDeliveries);
                System.out.println("Deleted " + associatedDeliveries.size() + " associated delivery records");
            }

            // Now delete the shipment (cargo will be cascaded automatically)
            shipmentRepository.deleteById(id);

            // Publish Kafka event
            String message = "Shipment deleted: ID=" + id;
            kafkaProducerService.sendMessage("shipment-events", message);
            notificationService.notifyShipmentDeleted(shipment);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shipment deleted successfully");
            response.put("shipmentId", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error deleting shipment: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting shipment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
