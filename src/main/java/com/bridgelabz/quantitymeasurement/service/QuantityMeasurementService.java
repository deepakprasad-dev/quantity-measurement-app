package com.bridgelabz.quantitymeasurement.service;

import com.bridgelabz.quantitymeasurement.dto.QuantityRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityResponseDTO;
import com.bridgelabz.quantitymeasurement.entity.QuantityRecord;
import com.bridgelabz.quantitymeasurement.exception.QuantityMeasurementException;
import com.bridgelabz.quantitymeasurement.model.*;
import com.bridgelabz.quantitymeasurement.repository.QuantityRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuantityMeasurementService {

    @Autowired
    private QuantityRecordRepository repository;


    public QuantityResponseDTO convertQuantity(QuantityRequestDTO request, String targetUnitStr) {
    log.info("Processing conversion request: Type={}, Value={}, From={}, To={}",
            request.getQuantityType(), request.getValue(), request.getUnit(), targetUnitStr);
        try {
            String type = request.getQuantityType().toUpperCase();
            String inputUnitStr = request.getUnit().toUpperCase();
            targetUnitStr = targetUnitStr.toUpperCase();

            double resultValue = 0.0;

            // 1. Route to the correct Enum based on the Type
            switch (type) {
                case "LENGTH":
                    resultValue = calculateConversion(request.getValue(), LengthUnit.valueOf(inputUnitStr), LengthUnit.valueOf(targetUnitStr));
                    break;
                case "VOLUME":
                    resultValue = calculateConversion(request.getValue(), VolumeUnit.valueOf(inputUnitStr), VolumeUnit.valueOf(targetUnitStr));
                    break;
                case "WEIGHT":
                    resultValue = calculateConversion(request.getValue(), WeightUnit.valueOf(inputUnitStr), WeightUnit.valueOf(targetUnitStr));
                    break;
                case "TEMPERATURE":
                    resultValue = calculateConversion(request.getValue(), TemperatureUnit.valueOf(inputUnitStr), TemperatureUnit.valueOf(targetUnitStr));
                    break;
                default:
                    throw new QuantityMeasurementException("Invalid Quantity Type. Use LENGTH, VOLUME, WEIGHT, or TEMPERATURE.");
            }

            // 2. Save to Database
            QuantityRecord record = new QuantityRecord();
            record.setQuantityType(type);
            record.setInputValue(request.getValue());
            record.setInputUnit(inputUnitStr);
            record.setTargetUnit(targetUnitStr);
            record.setResultValue(resultValue);
            repository.save(record);

            log.info("Successfully converted to {} {}. Saved to database.", resultValue, targetUnitStr);

            // 3. Return the generic response
            return new QuantityResponseDTO(resultValue, targetUnitStr, "Conversion Successful & Saved to DB!");

        } catch (IllegalArgumentException e) {
            log.error("Conversion failed due to invalid unit spellings: {}", e.getMessage());
            throw new QuantityMeasurementException("Failed to convert: Invalid unit spelling for the given category.");
        }
    }

    // --- THE GENERIC HELPER METHOD ---
    private <T extends IUnit> double calculateConversion(double value, T inputUnit, T targetUnit) {
        // Convert input to base unit, then convert base unit to target unit
        double baseValue = inputUnit.convertToBaseUnit(value);
        return targetUnit.convertFromBaseUnit(baseValue);
    }
}