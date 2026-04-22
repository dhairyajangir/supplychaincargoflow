package com.cargomanagement.repository;

import com.cargomanagement.models.Delivery;
import com.cargomanagement.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByShipment(Shipment shipment);
    Optional<Delivery> findByShipment(Shipment shipment);
    Optional<Delivery> findByShipmentShipmentId(Long shipmentId);
    List<Delivery> findByStatus(String status);
}