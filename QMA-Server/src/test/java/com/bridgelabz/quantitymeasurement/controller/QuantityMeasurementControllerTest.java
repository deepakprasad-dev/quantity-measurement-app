package com.bridgelabz.quantitymeasurement.controller;

import com.bridgelabz.quantitymeasurement.dto.OperationRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityRequestDTO;
import com.bridgelabz.quantitymeasurement.dto.QuantityResponseDTO;
import com.bridgelabz.quantitymeasurement.service.QuantityMeasurementService;
import com.bridgelabz.quantitymeasurement.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuantityMeasurementController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignore security just to test the controller logic
public class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuantityMeasurementService quantityMeasurementService;

    // We have to mock this because of security configurations
    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testConvertQuantity() throws Exception {
        // Create a dummy request
        QuantityRequestDTO req = new QuantityRequestDTO();
        req.setQuantityType("LENGTH");
        req.setValue(12.0);
        req.setUnit("INCH");

        // Create a dummy response that the service will return
        QuantityResponseDTO res = new QuantityResponseDTO();
        res.setResultValue(1.0);
        res.setTargetUnitStr("FEET");

        // Tell mockito what to do when the service is called
        Mockito.when(quantityMeasurementService.convertQuantity(any(QuantityRequestDTO.class), eq("FEET")))
                .thenReturn(res);

        // Convert the request object to JSON string
        String jsonRequest = objectMapper.writeValueAsString(req);

        // Send a POST request to our API and check if it works
        mockMvc.perform(post("/api/quantity/convert")
                        .param("targetUnit", "FEET")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(1.0))
                .andExpect(jsonPath("$.targetUnitStr").value("FEET"));
    }

    @Test
    public void testCompareQuantities() throws Exception {
        // Setup data
        OperationRequestDTO req = new OperationRequestDTO();
        req.setQuantityType("LENGTH");
        req.setFirstValue(12.0);
        req.setFirstUnit("INCH");
        req.setSecondValue(1.0);
        req.setSecondUnit("FEET");

        // Mock the service call
        Mockito.when(quantityMeasurementService.compareQuantities(any(OperationRequestDTO.class)))
                .thenReturn(true);

        String jsonRequest = objectMapper.writeValueAsString(req);

        // Make the request
        mockMvc.perform(post("/api/quantity/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testAddQuantities() throws Exception {
        // Setup data
        OperationRequestDTO req = new OperationRequestDTO();
        req.setQuantityType("LENGTH");
        req.setFirstValue(2.0);
        req.setFirstUnit("INCH");
        req.setSecondValue(2.0);
        req.setSecondUnit("INCH");
        req.setTargetUnit("INCH");

        QuantityResponseDTO res = new QuantityResponseDTO();
        res.setResultValue(4.0);
        res.setTargetUnitStr("INCH");

        // Mock service
        Mockito.when(quantityMeasurementService.addQuantities(any(OperationRequestDTO.class)))
                .thenReturn(res);

        String jsonRequest = objectMapper.writeValueAsString(req);

        // Execute API and check result
        mockMvc.perform(post("/api/quantity/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(4.0));
    }
}
