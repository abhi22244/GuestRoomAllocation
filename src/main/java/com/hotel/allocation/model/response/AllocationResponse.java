package com.hotel.allocation.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllocationResponse(
        @JsonProperty("usagePremium")
        int usagePremium,

        @JsonProperty("revenuePremium")
        double revenuePremium,

        @JsonProperty("usageEconomy")
        int usageEconomy,

        @JsonProperty("revenueEconomy")
        double revenueEconomy
) {}