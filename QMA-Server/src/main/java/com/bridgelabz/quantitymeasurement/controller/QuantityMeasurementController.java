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
        // errors are handled globally in GlobalExceptionHandler, no try-catch needed here
        QuantityResponseDTO response = service.convertQuantity(request, targetUnit);
        return ResponseEntity.ok(response);
    }
    // compare quantities
    @PostMapping("/compare")
    public ResponseEntity<Boolean> compare(@Valid @RequestBody OperationRequestDTO request) {
        boolean areEqual = service.compareQuantities(request);
        return ResponseEntity.ok(areEqual);
    }
    // add quantities
    @PostMapping("/add")
    public ResponseEntity<QuantityResponseDTO> add(@Valid @RequestBody OperationRequestDTO request) {
        QuantityResponseDTO response = service.addQuantities(request);
        return ResponseEntity.ok(response);
    }

    // subtract quantities
    @PostMapping("/subtract")
    public ResponseEntity<QuantityResponseDTO> subtract(@Valid @RequestBody OperationRequestDTO request) {
        QuantityResponseDTO response = service.subtractQuantities(request);
        return ResponseEntity.ok(response);
    }

    // divide quantity by a number
    @PostMapping("/divide")
    public ResponseEntity<QuantityResponseDTO> divide(@Valid @RequestBody OperationRequestDTO request) {
        QuantityResponseDTO response = service.divideQuantity(request);
        return ResponseEntity.ok(response);
    }

}