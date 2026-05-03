package com.bridgelabz.quantitymeasurement.service;

import com.bridgelabz.quantitymeasurement.dto.OperationRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityResponseDTO;
import com.bridgelabz.quantitymeasurement.entity.QuantityRecord;
import com.bridgelabz.quantitymeasurement.entity.User;
import com.bridgelabz.quantitymeasurement.exception.QuantityMeasurementException;
import com.bridgelabz.quantitymeasurement.model.*;
import com.bridgelabz.quantitymeasurement.repository.QuantityRecordRepository;
import com.bridgelabz.quantitymeasurement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuantityMeasurementService {

    @Autowired
    private QuantityRecordRepository repository;

    @Autowired
    private UserRepository userRepository;


    public QuantityResponseDTO convertQuantity(QuantityRequestDTO request, String targetUnitStr) {
        log.info("converting: type={}, value={}, from={}, to={}",
                request.getQuantityType(), request.getValue(), request.getUnit(), targetUnitStr);
        try {
            String type = request.getQuantityType().toUpperCase();
            String inputUnitStr = request.getUnit().toUpperCase();
            targetUnitStr = targetUnitStr.toUpperCase();

            double resultValue = 0.0;

            // figure out which category the user sent and do the conversion
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

            // get the currently logged-in user from the security context
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

            User loggedInUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new QuantityMeasurementException("Unauthorized: No valid user found in database."));

            // save the conversion to the database so we have a history
            QuantityRecord record = new QuantityRecord();
            record.setUser(loggedInUser);
            record.setQuantityType(type);
            record.setInputValue(request.getValue());
            record.setInputUnit(inputUnitStr);
            record.setTargetUnit(targetUnitStr);
            record.setResultValue(resultValue);
            repository.save(record);

            log.info("done. result={} {}", resultValue, targetUnitStr);

            return new QuantityResponseDTO(resultValue, targetUnitStr, "Conversion Successful & Saved to DB!");

        } catch (IllegalArgumentException e) {
            log.error("conversion failed, bad unit spelling: {}", e.getMessage());
            throw new QuantityMeasurementException("Failed to convert: Invalid unit spelling for the given category.");
        }
    }

    // converts a value from one unit to another using the base unit as a middleman
    private <T extends IUnit> double calculateConversion(double value, T inputUnit, T targetUnit) {
        double baseValue = inputUnit.convertToBaseUnit(value);
        return targetUnit.convertFromBaseUnit(baseValue);
    }


    public boolean compareQuantities(OperationRequestDTO request) {
        String type = request.getQuantityType().toUpperCase();

        switch (type) {
            case "LENGTH":
                return compareGeneric(request, LengthUnit.valueOf(request.getFirstUnit().toUpperCase()), LengthUnit.valueOf(request.getSecondUnit().toUpperCase()));
            case "VOLUME":
                return compareGeneric(request, VolumeUnit.valueOf(request.getFirstUnit().toUpperCase()), VolumeUnit.valueOf(request.getSecondUnit().toUpperCase()));
            case "WEIGHT":
                return compareGeneric(request, WeightUnit.valueOf(request.getFirstUnit().toUpperCase()), WeightUnit.valueOf(request.getSecondUnit().toUpperCase()));
            case "TEMPERATURE":
                return compareGeneric(request, TemperatureUnit.valueOf(request.getFirstUnit().toUpperCase()), TemperatureUnit.valueOf(request.getSecondUnit().toUpperCase()));
            default:
                throw new QuantityMeasurementException("Invalid Quantity Type.");
        }
    }

    // uses our custom equals() on the Quantity model to do the comparison
    private <T extends IUnit> boolean compareGeneric(OperationRequestDTO req, T unit1, T unit2) {
        Quantity<T> q1 = Quantity.of(req.getFirstValue(), unit1);
        Quantity<T> q2 = Quantity.of(req.getSecondValue(), unit2);
        return q1.equals(q2);
    }


    public QuantityResponseDTO addQuantities(OperationRequestDTO request) {
        String type = request.getQuantityType().toUpperCase();
        // default to first unit if target not provided
        String targetStr = request.getTargetUnit() != null ? request.getTargetUnit().toUpperCase() : request.getFirstUnit().toUpperCase();

        double resultValue = 0.0;

        switch (type) {
            case "LENGTH":
                resultValue = addGeneric(request, LengthUnit.valueOf(request.getFirstUnit().toUpperCase()), LengthUnit.valueOf(request.getSecondUnit().toUpperCase()), LengthUnit.valueOf(targetStr));
                break;
            case "VOLUME":
                resultValue = addGeneric(request, VolumeUnit.valueOf(request.getFirstUnit().toUpperCase()), VolumeUnit.valueOf(request.getSecondUnit().toUpperCase()), VolumeUnit.valueOf(targetStr));
                break;
            case "WEIGHT":
                resultValue = addGeneric(request, WeightUnit.valueOf(request.getFirstUnit().toUpperCase()), WeightUnit.valueOf(request.getSecondUnit().toUpperCase()), WeightUnit.valueOf(targetStr));
                break;
            default:
                throw new QuantityMeasurementException("Cannot add Temperature or Invalid Type.");
        }

        return new QuantityResponseDTO(resultValue, targetStr, "Addition Successful!");
    }

    // helper to add two quantities and return just the result value
    private <T extends IUnit> double addGeneric(OperationRequestDTO req, T unit1, T unit2, T targetUnit) {
        Quantity<T> q1 = Quantity.of(req.getFirstValue(), unit1);
        Quantity<T> q2 = Quantity.of(req.getSecondValue(), unit2);
        Quantity<T> result = q1.add(q2, targetUnit);
        return result.getValue();
    }
}