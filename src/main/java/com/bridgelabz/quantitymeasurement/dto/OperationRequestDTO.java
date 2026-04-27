package com.bridgelabz.quantitymeasurement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequestDTO {


    private String quantityType;


    private Double firstValue;


    private String firstUnit;


    private Double secondValue;


    private String secondUnit;


    private String targetUnit;
}