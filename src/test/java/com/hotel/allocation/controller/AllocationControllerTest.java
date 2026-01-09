package com.hotel.allocation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.allocation.model.request.AllocationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AllocationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCalculateOccupancy_Success() throws Exception {
        AllocationRequest request = new AllocationRequest(
                7,
                5,
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        mockMvc.perform(post("/api/v1/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usagePremium").value(6))
                .andExpect(jsonPath("$.revenuePremium").value(1054.0))
                .andExpect(jsonPath("$.usageEconomy").value(4))
                .andExpect(jsonPath("$.revenueEconomy").value(189.99));
    }

    @Test
    void testCalculateOccupancy_InvalidRequest() throws Exception {
        // Test with negative room count
        AllocationRequest request = new AllocationRequest(-1, 5, Arrays.asList(100.0));

        mockMvc.perform(post("/api/v1/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCalculateOccupancy_EmptyGuests() throws Exception {
        AllocationRequest request = new AllocationRequest(5, 5, Arrays.asList());

        mockMvc.perform(post("/api/v1/occupancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usagePremium").value(0))
                .andExpect(jsonPath("$.revenuePremium").value(0.0))
                .andExpect(jsonPath("$.usageEconomy").value(0))
                .andExpect(jsonPath("$.revenueEconomy").value(0.0));
    }
}