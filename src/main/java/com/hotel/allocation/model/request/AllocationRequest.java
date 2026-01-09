package com.hotel.allocation.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AllocationRequest(
        @NotNull(message = "Premium rooms cannot be null")
        @Min(value = 0, message = "Premium rooms cannot be negative")
        Integer premiumRooms,

        @NotNull(message = "Economy rooms cannot be null")
        @Min(value = 0, message = "Economy rooms cannot be negative")
        Integer economyRooms,

        @NotNull(message = "Potential guests cannot be null")
        @Size(min = 0, message = "Potential guests list cannot be empty")
        List<Double> potentialGuests
) {}