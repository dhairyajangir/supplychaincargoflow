package com.cargomanagement.controller;

import com.cargomanagement.dto.CargoCreateRequest;
import com.cargomanagement.models.Cargo;
import com.cargomanagement.models.Shipment;
import com.cargomanagement.repository.CargoRepository;
import com.cargomanagement.repository.ShipmentRepository;
import com.cargomanagement.service.KafkaProducerService;
import com.cargomanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cargo")
public class CargoController {

    private final CargoRepository cargoRepository;
    private final ShipmentRepository shipmentRepository;
    private final KafkaProducerService kafkaProducerService;
    private final NotificationService notificationService;

    @Autowired
    public CargoController(CargoRepository cargoRepository,
                           ShipmentRepository shipmentRepository,
                           KafkaProducerService kafkaProducerService,
                           NotificationService notificationService) {
        this.cargoRepository = cargoRepository;
        this.shipmentRepository = shipmentRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Cargo> getAllCargo() {
        return cargoRepository.findAllWithShipment(); // Use the new method here
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createCargo(@RequestBody CargoCreateRequest request) {
        try {
            System.out.println("Received cargo request: " + request);
            
            // Create new Cargo entity
            Cargo cargo = new Cargo();
            cargo.setType(request.getType());
            cargo.setWeight(request.getWeight());
            cargo.setValue(request.getValue());
            cargo.setVolume(request.getVolume());
            cargo.setWeightUnit(request.getWeightUnit());
            cargo.setDescription(request.getDescription());
            
            // Handle shipment relationship if provided
            if (request.getShipmentId() != null) {
                Shipment shipment = shipmentRepository.findById(request.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + request.getShipmentId()));
                cargo.setShipment(shipment);
            }
            
            Cargo savedCargo = cargoRepository.save(cargo);
            String message = "Cargo created: ID=" + savedCargo.getCargoId() + 
                           ", Type=" + savedCargo.getType() + 
                           ", Weight=" + savedCargo.getWeight() + "kg" + 
                           ", Value=$" + savedCargo.getValue();
            kafkaProducerService.sendMessage("cargo-events", message);
            notificationService.notifyCargoCreated(savedCargo);
            return ResponseEntity.ok(savedCargo);
        } catch (Exception e) {
            System.err.println("Error creating cargo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating cargo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> getCargoById(@PathVariable Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));
        return ResponseEntity.ok(cargo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCargo(@PathVariable Long id, @RequestBody CargoCreateRequest request) {
        try {
            Cargo cargo = cargoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));

            // Update all fields
            cargo.setType(request.getType());
            cargo.setWeight(request.getWeight());
            cargo.setValue(request.getValue());
            cargo.setVolume(request.getVolume());
            cargo.setWeightUnit(request.getWeightUnit());
            cargo.setDescription(request.getDescription());
            
            // Handle shipment relationship if provided
            if (request.getShipmentId() != null) {
                Shipment shipment = shipmentRepository.findById(request.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + request.getShipmentId()));
                cargo.setShipment(shipment);
            } else {
                cargo.setShipment(null);
            }

            final Cargo updatedCargo = cargoRepository.save(cargo);
            String message = "Cargo updated: ID=" + id + ", Type=" + updatedCargo.getType();
            kafkaProducerService.sendMessage("cargo-events", message);
            notificationService.notifyCargoUpdated(updatedCargo);
            return ResponseEntity.ok(updatedCargo);
        } catch (Exception e) {
            System.err.println("Error updating cargo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating cargo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCargo(@PathVariable Long id) {
        try {
            Cargo cargo = cargoRepository.findById(id).orElse(null);
            if (cargo == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Cargo not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            cargoRepository.delete(cargo);

            // Publish to Kafka
            String message = "Cargo deleted: ID=" + id;
            kafkaProducerService.sendMessage("cargo-events", message);
            notificationService.notifyCargoDeleted(cargo);

            // Return success response with JSON body
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cargo deleted successfully");
            response.put("cargoId", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting cargo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
