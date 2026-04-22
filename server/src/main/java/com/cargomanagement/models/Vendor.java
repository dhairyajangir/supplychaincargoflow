package com.cargomanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "vendor")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    @NotBlank(message = "Name is required and cannot be empty")
    @Size(min = 1, max = 100, message = "Name must be between 1-100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 500, message = "Contact info cannot exceed 500 characters")
    @Column(name = "contact_info", nullable = true, length = 500)
    private String contactInfo;

    @NotBlank(message = "Service type is required")
    @Size(max = 100, message = "Service type cannot exceed 100 characters")
    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;  // e.g., "Logistics", "Shipping", "Transportation", etc.

    @Column(name = "is_active", nullable = true)
    private Boolean isActive = true;  // Default to active

    @CreationTimestamp  // Auto-sets on persist
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}