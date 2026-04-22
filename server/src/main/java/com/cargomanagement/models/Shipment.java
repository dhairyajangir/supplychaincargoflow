package com.cargomanagement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "shipment")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentId;

    @NotBlank(message = "Origin is required")
    @Size(max = 100, message = "Origin cannot exceed 100 characters")
    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination cannot exceed 100 characters")
    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 50)  // Increased length for longer status names
    private String status = "Created";  // Java default for insert

    @Column(name = "estimated_delivery", nullable = true)  // DATE type
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate estimatedDelivery;  // Using LocalDate for DATE

    // FK to Route (NULLABLE - Optional assignment)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = true)
    private Route assignedRoute;

    // FK to Vendor (NULLABLE - Optional assignment)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", nullable = true)
    private Vendor assignedVendor;

    @Size(max = 20, message = "Shipment code cannot exceed 20 characters")
    @Column(name = "shipment_code", unique = true, length = 20, nullable = true)  // UNIQUE but NULLABLE
    private String shipmentCode;  // Optional tracking code

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Cargo> cargoItems;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}