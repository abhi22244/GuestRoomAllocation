package com.hotel.allocation.controller;

import com.hotel.allocation.model.request.AllocationRequest;
import com.hotel.allocation.model.response.AllocationResponse;
import com.hotel.allocation.service.RoomAllocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AllocationController {

    private final RoomAllocationService roomAllocationService;

    public AllocationController(RoomAllocationService roomAllocationService) {
        this.roomAllocationService = roomAllocationService;
    }

    @PostMapping("/occupancy")
    public ResponseEntity<AllocationResponse> calculateOccupancy(
            @Valid @RequestBody AllocationRequest request) {

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);
        return ResponseEntity.ok(response);
    }
}