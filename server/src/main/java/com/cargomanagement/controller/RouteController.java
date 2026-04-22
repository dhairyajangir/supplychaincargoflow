package com.cargomanagement.controller;

import com.cargomanagement.models.Route;
import com.cargomanagement.repository.RouteRepository;
import com.cargomanagement.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteRepository routeRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public RouteController(RouteRepository routeRepository, KafkaProducerService kafkaProducerService) {
        this.routeRepository = routeRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @PostMapping
    public Route createRoute(@RequestBody Route route) {
        Route savedRoute = routeRepository.save(route);
        String message = "Route created: ID=" + savedRoute.getRouteId() + ", From=" + savedRoute.getOriginPort() + " to " + savedRoute.getDestinationPort();
        kafkaProducerService.sendMessage("route-events", message);
        return savedRoute;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody Route routeDetails) {
        try {
            Route route = routeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));

            // Update all fields
            route.setOriginPort(routeDetails.getOriginPort());
            route.setDestinationPort(routeDetails.getDestinationPort());
            route.setDistance(routeDetails.getDistance());
            route.setDuration(routeDetails.getDuration());
            route.setTransportationMode(routeDetails.getTransportationMode());
            route.setCost(routeDetails.getCost());
            route.setStatus(routeDetails.getStatus());

            final Route updatedRoute = routeRepository.save(route);
            String message = "Route updated: ID=" + id + ", From=" + updatedRoute.getOriginPort() + " to " + updatedRoute.getDestinationPort();
            kafkaProducerService.sendMessage("route-events", message);
            return ResponseEntity.ok(updatedRoute);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating route: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoute(@PathVariable Long id) {
        try {
            if (!routeRepository.existsById(id)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Route not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Check if route is being used by any shipments
            long shipmentCount = routeRepository.countShipmentsUsingRoute(id);
            if (shipmentCount > 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Cannot delete route: This route is currently assigned to " + 
                                           shipmentCount + " shipment(s). Please reassign or delete those shipments first.");
                errorResponse.put("shipmentCount", shipmentCount);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            routeRepository.deleteById(id);

            // Publish to Kafka
            String message = "Route deleted: ID=" + id;
            kafkaProducerService.sendMessage("route-events", message);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Route deleted successfully");
            response.put("routeId", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting route: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
