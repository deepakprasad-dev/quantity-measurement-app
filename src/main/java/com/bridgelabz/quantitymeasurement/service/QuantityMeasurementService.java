package com.bridgelabz.quantitymeasurement.service;

import com.bridgelabz.quantitymeasurement.dto.QuantityRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityResponseDTO;
import com.bridgelabz.quantitymeasurement.model.LengthUnit;
import com.bridgelabz.quantitymeasurement.model.Quantity;
import org.springframework.stereotype.Service;

@Service
public class QuantityMeasurementService {

    public QuantityResponseDTO convertLength(QuantityRequestDTO request, String targetUnitStr) {

        
        LengthUnit inputUnit = LengthUnit.valueOf(request.getUnit().toUpperCase());
        LengthUnit targetUnit = LengthUnit.valueOf(targetUnitStr.toUpperCase());

        // Create the Domain Object
        Quantity<LengthUnit> inputQuantity = Quantity.of(request.getValue(), inputUnit);


        Quantity<LengthUnit> zeroTarget = Quantity.of(0.0, targetUnit);
        Quantity<LengthUnit> result = inputQuantity.add(zeroTarget, targetUnit);

        // result back into a Response DTO
        return new QuantityResponseDTO(
                result.getValue(),
                targetUnit.name(),
                "Conversion Successful!"
        );
    }
}