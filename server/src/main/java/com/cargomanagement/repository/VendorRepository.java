package com.cargomanagement.repository;

import com.cargomanagement.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findByServiceType(String serviceType);
    List<Vendor> findByIsActive(Boolean isActive);
}