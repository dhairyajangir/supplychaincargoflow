package com.cargomanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.cargomanagement.repository.*;
import com.cargomanagement.models.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private VendorRepository vendorRepository;

    @GetMapping("/metrics")
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Count metrics
        metrics.put("totalShipments", shipmentRepository.count());
        metrics.put("activeCargo", cargoRepository.count());
        metrics.put("availableRoutes", routeRepository.count());
        metrics.put("partnerVendors", vendorRepository.count());
        
        // Status distribution
        Map<String, Long> shipmentStatuses = new HashMap<>();
        List<Shipment> shipments = shipmentRepository.findAll();
        for (Shipment shipment : shipments) {
            String status = shipment.getStatus();
            shipmentStatuses.put(status, shipmentStatuses.getOrDefault(status, 0L) + 1);
        }
        metrics.put("shipmentStatuses", shipmentStatuses);
        
        // Route status distribution
        Map<String, Long> routeStatuses = new HashMap<>();
        List<Route> routes = routeRepository.findAll();
        for (Route route : routes) {
            String status = route.getStatus();
            routeStatuses.put(status, routeStatuses.getOrDefault(status, 0L) + 1);
        }
        metrics.put("routeStatuses", routeStatuses);
        
        return metrics;
    }

    @GetMapping("/recent-activities")
    public List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Get recent shipments (get all and limit in code for simplicity)
        List<Shipment> allShipments = shipmentRepository.findAll();
        List<Shipment> recentShipments = allShipments.subList(0, Math.min(10, allShipments.size()));
        
        for (Shipment shipment : recentShipments) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", shipment.getShipmentId().toString());
            activity.put("shipmentId", "SH" + String.format("%03d", shipment.getShipmentId()));
            activity.put("action", getActionFromStatus(shipment.getStatus()));
            activity.put("timestamp", "recently");
            activity.put("type", getActivityType(shipment.getStatus()));
            activity.put("status", shipment.getStatus().toLowerCase().replace(" ", "-"));
            activities.add(activity);
        }
        
        return activities;
    }

    private String getActionFromStatus(String status) {
        switch (status.toLowerCase()) {
            case "created":
                return "has been created";
            case "in transit":
                return "is in transit";
            case "delivered":
                return "has been delivered";
            case "picked up":
                return "has been picked up";
            default:
                return "status updated";
        }
    }

    private String getActivityType(String status) {
        switch (status.toLowerCase()) {
            case "created":
                return "created";
            case "in transit":
                return "in-transit";
            case "delivered":
                return "delivered";
            case "picked up":
                return "picked-up";
            default:
                return "status-update";
        }
    }
}
