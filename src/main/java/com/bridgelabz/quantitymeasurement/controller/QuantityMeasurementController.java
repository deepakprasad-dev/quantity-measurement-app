package com.bridgelabz.quantitymeasurement.controller;

import com.bridgelabz.quantitymeasurement.dto.OperationRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityResponseDTO;
import com.bridgelabz.quantitymeasurement.service.QuantityMeasurementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quantity")
@CrossOrigin(origins = "*") // Crucial: Allows your React frontend to talk to this API!
public class QuantityMeasurementController {

    @Autowired
    private QuantityMeasurementService service;

    @PostMapping("/convert")
    public ResponseEntity<QuantityResponseDTO> convert(
            @Valid @RequestBody QuantityRequestDTO request,
            @RequestParam String targetUnit) {

        // Changed from convertLength to convertQuantity
        QuantityResponseDTO response = service.convertQuantity( request, targetUnit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/compare")
    public ResponseEntity<Boolean> compare(@Valid @RequestBody OperationRequestDTO request) {
        boolean areEqual = service.compareQuantities(request);
        return ResponseEntity.ok(areEqual);
    }

    @PostMapping("/add")
    public ResponseEntity<QuantityResponseDTO> add(@Valid @RequestBody OperationRequestDTO request) {
        QuantityResponseDTO response = service.addQuantities(request);
        return ResponseEntity.ok(response);
    }

}