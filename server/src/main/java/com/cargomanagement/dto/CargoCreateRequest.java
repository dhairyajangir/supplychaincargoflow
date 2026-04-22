package com.cargomanagement.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CargoCreateRequest {
    private String type;
    private BigDecimal weight;
    private BigDecimal value;
    private BigDecimal volume;
    private String weightUnit;
    private String description;
    private Long shipmentId; // Just the ID, not the full object
}