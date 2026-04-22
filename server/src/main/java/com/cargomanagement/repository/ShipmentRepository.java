package com.cargomanagement.repository;

import com.cargomanagement.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByStatus(String status);
    
    @Query("SELECT s FROM Shipment s LEFT JOIN FETCH s.cargoItems LEFT JOIN FETCH s.assignedRoute LEFT JOIN FETCH s.assignedVendor")
    List<Shipment> findAllWithDetails();
}