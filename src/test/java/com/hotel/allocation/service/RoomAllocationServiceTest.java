package com.hotel.allocation.service;

import com.hotel.allocation.model.request.AllocationRequest;
import com.hotel.allocation.model.response.AllocationResponse;
import com.hotel.allocation.service.impl.RoomAllocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomAllocationServiceTest {

    private RoomAllocationService roomAllocationService;

    @BeforeEach
    void setUp() {
        roomAllocationService = new RoomAllocationServiceImpl();
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // Test Case 1
                Arguments.of(
                        3, // premiumRooms
                        3, // economyRooms
                        Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0),
                        3, // expectedPremiumUsage
                        738.0, // expectedPremiumRevenue
                        3, // expectedEconomyUsage
                        167.99 // expectedEconomyRevenue
                ),
                // Test Case 2
                Arguments.of(
                        7,
                        5,
                        Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0),
                        6,
                        1054.0,
                        4,
                        189.99
                ),
                // Test Case 3
                Arguments.of(
                        2,
                        7,
                        Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0),
                        2,
                        583.0,
                        4,
                        189.99
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testCalculateOptimalAllocation(int premiumRooms, int economyRooms, List<Double> guests,
                                        int expectedPremiumUsage, double expectedPremiumRevenue,
                                        int expectedEconomyUsage, double expectedEconomyRevenue) {
        // Given
        AllocationRequest request = new AllocationRequest(premiumRooms, economyRooms, guests);

        // When
        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Then
        assertEquals(expectedPremiumUsage, response.usagePremium());
        assertEquals(expectedPremiumRevenue, response.revenuePremium());
        assertEquals(expectedEconomyUsage, response.usageEconomy());
        assertEquals(expectedEconomyRevenue, response.revenueEconomy());
    }

    @Test
    void testUpgradeScenario() {
        // Test scenario where economy guests get upgraded to premium rooms
        AllocationRequest request = new AllocationRequest(
                7,
                1,
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // 6 premium guests + 1 upgraded economy guest (99.99)
        assertEquals(7, response.usagePremium());
        assertEquals(1153.99, response.revenuePremium());
        assertEquals(1, response.usageEconomy());
        assertEquals(45.0, response.revenueEconomy());
    }

    @Test
    void testNoGuests() {
        AllocationRequest request = new AllocationRequest(5, 5, Arrays.asList());

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testMoreRoomsThanGuests() {
        AllocationRequest request = new AllocationRequest(
                10,
                10,
                Arrays.asList(150.0, 80.0, 90.0, 200.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(2, response.usagePremium());
        assertEquals(350.0, response.revenuePremium());
        assertEquals(2, response.usageEconomy());
        assertEquals(170.0, response.revenueEconomy());
    }
}