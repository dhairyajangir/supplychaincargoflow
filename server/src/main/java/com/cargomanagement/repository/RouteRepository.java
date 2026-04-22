package com.cargomanagement.repository;

import com.cargomanagement.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByStatus(String status);
    List<Route> findByOriginPortAndDestinationPort(String originPort, String destinationPort);
    List<Route> findByTransportationMode(String transportationMode);
    
    // Check if route is being used by any shipments before deletion
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.assignedRoute.routeId = :routeId")
    long countShipmentsUsingRoute(@Param("routeId") Long routeId);
}