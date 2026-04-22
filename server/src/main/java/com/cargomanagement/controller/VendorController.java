package com.cargomanagement.controller;

import com.cargomanagement.models.Vendor;
import com.cargomanagement.repository.VendorRepository;
import com.cargomanagement.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorRepository vendorRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public VendorController(VendorRepository vendorRepository, KafkaProducerService kafkaProducerService) {
        this.vendorRepository = vendorRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {
        try {
            System.out.println("Received vendor request: " + vendor);
            
            // Set default for isActive if not provided
            if (vendor.getIsActive() == null) {
                vendor.setIsActive(true);
            }
            
            Vendor savedVendor = vendorRepository.save(vendor);
            String message = "Vendor created: ID=" + savedVendor.getVendorId() + ", Name=" + savedVendor.getName();
            kafkaProducerService.sendMessage("vendor-events", message);
            return ResponseEntity.ok(savedVendor);
        } catch (Exception e) {
            System.err.println("Error creating vendor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating vendor: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        return ResponseEntity.ok(vendor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @RequestBody Vendor vendorDetails) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        vendor.setName(vendorDetails.getName());
        vendor.setContactInfo(vendorDetails.getContactInfo());
        vendor.setServiceType(vendorDetails.getServiceType());

        final Vendor updatedVendor = vendorRepository.save(vendor);
        String message = "Vendor updated: ID=" + id + ", Name=" + updatedVendor.getName();
        kafkaProducerService.sendMessage("vendor-events", message);
        return ResponseEntity.ok(updatedVendor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVendor(@PathVariable Long id) {
        try {
            if (!vendorRepository.existsById(id)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Vendor not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            vendorRepository.deleteById(id);

            // Publish to Kafka
            String message = "Vendor deleted: ID=" + id;
            kafkaProducerService.sendMessage("vendor-events", message);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vendor deleted successfully");
            response.put("vendorId", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting vendor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
