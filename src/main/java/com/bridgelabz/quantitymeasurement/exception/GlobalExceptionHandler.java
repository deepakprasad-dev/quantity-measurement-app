package com.bridgelabz.quantitymeasurement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // custom business exceptions
    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, String>> handleQuantityException(QuantityMeasurementException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // catches the exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();

        if (ex.getMessage().contains("No enum constant")) {
            response.put("message", "Invalid Unit Type Provided. Please check your spelling.");
        } else {
            response.put("message", ex.getMessage());
        }
        response.put("error", "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}