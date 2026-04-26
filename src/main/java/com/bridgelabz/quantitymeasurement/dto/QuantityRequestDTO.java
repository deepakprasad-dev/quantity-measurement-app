package com.bridgelabz.quantitymeasurement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityRequestDTO {
    private String quantityType;
    private double value;
    private String unit;
}