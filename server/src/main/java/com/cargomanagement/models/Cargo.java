package com.cargomanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cargoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_id", nullable = true)
    private Shipment shipment;

    @NotBlank(message = "Type is required")
    @Column(name = "type", nullable = false, length = 50)
    private String type;  // String for type

    @Column(name = "value", nullable = false, precision = 12, scale = 2)  // DECIMAL(12,2)
    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", message = "Value must be >= 0")
    private BigDecimal value;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(name = "description", length = 255)  // TEXT
    private String description;

    @PositiveOrZero(message = "Weight must be positive or zero")
    @Column(name = "weight", precision = 10, scale = 2)  // DECIMAL(10,2) allows upto 9999999999.99.
    private BigDecimal weight;  // Allow NULL per specs

    @PositiveOrZero(message = "Volume must be positive or zero")
    @Column(name = "volume", precision = 10, scale = 2) // DECIMAL(10,2)
    private BigDecimal volume;  // Added from specs

    @Column(name = "weight_unit", length = 10)  // Not in original specs, but from context
    private String weightUnit;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}