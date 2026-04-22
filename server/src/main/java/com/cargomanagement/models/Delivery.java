package com.cargomanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @NotNull(message = "Shipment is required")
    @OneToOne
    @JoinColumn(name = "shipment_id", nullable = false)  // NOT NULL
    private Shipment shipment;

    @Column(name = "actual_delivery_date", nullable = true)  // TIMESTAMP, nullable
    private LocalDateTime actualDeliveryDate;  // Use LocalDateTime for TIMESTAMP

    @NotBlank(message = "Recipient is required and cannot be empty")
    @Size(min = 1, max = 100, message = "Recipient must be 1-100 characters")
    @Column(name = "recipient", nullable = false, length = 100)
    private String recipient;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Pending|Delivered|Failed", message = "Status must be Pending, Delivered, or Failed")
    @Column(name = "status", nullable = false, length = 20)  // Default 'Pending' via field init if needed
    private String status = "Pending";  // Java default for insert

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}