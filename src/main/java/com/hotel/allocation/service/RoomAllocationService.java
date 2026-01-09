package com.hotel.allocation.service;

import com.hotel.allocation.model.request.AllocationRequest;
import com.hotel.allocation.model.response.AllocationResponse;

public interface RoomAllocationService {
    AllocationResponse calculateOptimalAllocation(AllocationRequest request);
}