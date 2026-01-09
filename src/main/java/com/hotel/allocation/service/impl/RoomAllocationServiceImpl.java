package com.hotel.allocation.service.impl;

import com.hotel.allocation.model.request.AllocationRequest;
import com.hotel.allocation.model.response.AllocationResponse;
import com.hotel.allocation.service.RoomAllocationService;
import com.hotel.allocation.util.GuestAllocator;
import org.springframework.stereotype.Service;

@Service
public class RoomAllocationServiceImpl implements RoomAllocationService {

    @Override
    public AllocationResponse calculateOptimalAllocation(AllocationRequest request) {
        GuestAllocator.AllocationResult result = GuestAllocator.allocateRooms(
                request.premiumRooms(),
                request.economyRooms(),
                request.potentialGuests()
        );

        return new AllocationResponse(
                result.getPremiumAllocated(),
                result.getPremiumRevenue(),
                result.getEconomyAllocated(),
                result.getEconomyRevenue()
        );
    }
}