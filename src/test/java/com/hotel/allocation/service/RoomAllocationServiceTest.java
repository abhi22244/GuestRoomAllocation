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
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RoomAllocationServiceTest {

    private RoomAllocationService roomAllocationService;

    @BeforeEach
    void setUp() {
        roomAllocationService = new RoomAllocationServiceImpl();
    }

    // ========== ORIGINAL TEST CASES ==========

    @Test
    void testOriginalCase1() {
        AllocationRequest request = new AllocationRequest(
                3, // premiumRooms
                3, // economyRooms
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(3, response.usagePremium());
        assertEquals(738.0, response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(167.99, response.revenueEconomy());
    }

    @Test
    void testOriginalCase2() {
        AllocationRequest request = new AllocationRequest(
                7,
                5,
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(6, response.usagePremium());
        assertEquals(1054.0, response.revenuePremium());
        assertEquals(4, response.usageEconomy());
        assertEquals(189.99, response.revenueEconomy());
    }

    @Test
    void testOriginalCase3() {
        AllocationRequest request = new AllocationRequest(
                2,
                7,
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(2, response.usagePremium());
        assertEquals(583.0, response.revenuePremium());
        assertEquals(4, response.usageEconomy());
        assertEquals(189.99, response.revenueEconomy());
    }

    // ========== EDGE CASE TEST CASES ==========

    @Test
    void testNoGuests() {
        AllocationRequest request = new AllocationRequest(5, 5, Collections.emptyList());

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testNoRooms() {
        AllocationRequest request = new AllocationRequest(
                0,
                0,
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testOnlyPremiumGuests() {
        AllocationRequest request = new AllocationRequest(
                3,
                3,
                Arrays.asList(100.0, 150.0, 200.0, 250.0, 300.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Should allocate 3 premium rooms to highest paying premium guests
        assertEquals(3, response.usagePremium());
        assertEquals(300.0 + 250.0 + 200.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testOnlyEconomyGuests() {
        AllocationRequest request = new AllocationRequest(
                3,
                3,
                Arrays.asList(23.0, 45.0, 67.0, 89.0, 99.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Should allocate 3 economy rooms to highest paying economy guests
        assertEquals(2, response.usagePremium());
        assertEquals(99.0 + 89.0 , response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(23.0 + 45.0 + 67.0 , response.revenueEconomy());
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

    @Test
    void testUpgradeScenarioEconomyFull() {
        // Premium rooms: 7, Economy rooms: 1
        // Premium guests: 6, Economy guests: 4
        // Economy rooms full (1 room, 4 guests)
        // Should upgrade highest economy guest (99.99) to premium
        AllocationRequest request = new AllocationRequest(
                7, // premiumRooms
                1, // economyRooms
                Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium: 6 premium guests + 1 upgraded (99.99) = 7 rooms
        // Premium revenue: 374 + 209 + 155 + 115 + 101 + 100 + 99.99 = 1153.99
        // Economy: 1 room with 45 = 45.0
        assertEquals(7, response.usagePremium());
        assertEquals(1153.99, response.revenuePremium());
        assertEquals(1, response.usageEconomy());
        assertEquals(45.0, response.revenueEconomy());
    }

    @Test
    void testMultipleUpgrades() {
        // Premium rooms: 4, Economy rooms: 2
        // Premium guests: 2, Economy guests: 5
        // Should upgrade 2 highest economy guests
        AllocationRequest request = new AllocationRequest(
                4, // premiumRooms
                2, // economyRooms
                Arrays.asList(50.0, 60.0, 70.0, 80.0, 90.0, 150.0, 200.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium: 2 premium guests + 2 upgraded (90, 80) = 4 rooms
        // Premium revenue: 200 + 150 + 90 + 80 = 520.0
        // Economy: 2 rooms with 70, 60 = 130.0
        assertEquals(4, response.usagePremium());
        assertEquals(520.0, response.revenuePremium());
        assertEquals(2, response.usageEconomy());
        assertEquals(130.0, response.revenueEconomy());
    }

    @Test
    void testNoUpgradeWhenEconomyNotFull() {
        // Premium rooms: 5, Economy rooms: 5
        // Premium guests: 3, Economy guests: 3
        // Economy not full, so no upgrade even though premium rooms available
        AllocationRequest request = new AllocationRequest(
                5,
                5,
                Arrays.asList(150.0, 120.0, 110.0, 90.0, 80.0, 70.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(3, response.usagePremium());
        assertEquals(380.0, response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(240.0, response.revenueEconomy());
    }

    @Test
    void testBoundaryAt100() {
        // Test guests exactly at 100 boundary
        AllocationRequest request = new AllocationRequest(
                5,
                5,
                Arrays.asList(100.0, 99.99, 100.01, 99.0, 100.0, 100.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Guests >= 100: 100.0, 100.01, 100.0, 100.0 (4 guests)
        // Guests < 100: 99.99, 99.0 (2 guests)
        assertEquals(4, response.usagePremium());
        assertEquals(400.01, response.revenuePremium()); // 100 + 100.01 + 100 + 100
        assertEquals(2, response.usageEconomy());
        assertEquals(198.99, response.revenueEconomy()); // 99.99 + 99.0
    }

    @Test
    void testAllEconomyUpgraded() {
        // Only economy guests, but all get upgraded to premium
        AllocationRequest request = new AllocationRequest(
                5, // premiumRooms
                0, // economyRooms (full by definition)
                Arrays.asList(99.99, 98.0, 97.0, 96.0, 95.0, 94.0) // 6 economy guests
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Should upgrade 5 highest economy guests to premium
        assertEquals(5, response.usagePremium());
        assertEquals(99.99 + 98.0 + 97.0 + 96.0 + 95.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testLargeNumberOfGuests() {
        // Test with many guests
        List<Double> manyGuests = Arrays.asList(
                1000.0, 950.0, 900.0, 850.0, 800.0,  // 5 premium
                75.0, 74.0, 73.0, 72.0, 71.0,        // 5 economy
                70.0, 69.0, 68.0, 67.0, 66.0         // 5 more economy
        );

        AllocationRequest request = new AllocationRequest(
                6, // premiumRooms
                5, // economyRooms
                manyGuests
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium: 5 premium guests + 1 upgrade (75.0) = 6 rooms
        // Premium revenue: 1000 + 950 + 900 + 850 + 800 + 75 = 4575.0
        // Economy: 5 rooms with 74, 73, 72, 71, 70 = 360.0
        assertEquals(6, response.usagePremium());
        assertEquals(4575.0, response.revenuePremium());
        assertEquals(5, response.usageEconomy());
        assertEquals(360.0, response.revenueEconomy());
    }

    @Test
    void testDuplicatePrices() {
        // Test with duplicate prices
        AllocationRequest request = new AllocationRequest(
                3,
                3,
                Arrays.asList(100.0, 100.0, 100.0, 99.99, 99.99, 99.99)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // All premium guests get premium rooms, all economy get economy rooms
        assertEquals(3, response.usagePremium());
        assertEquals(300.0, response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(299.97, response.revenueEconomy()); // 3 * 99.99
    }

    @Test
    void testOnlyPremiumRoomsAvailable() {
        // Only premium rooms, no economy rooms
        AllocationRequest request = new AllocationRequest(
                5, // premiumRooms
                0, // economyRooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 97.0, 100.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests: 150, 120, 100 (3 guests) get premium rooms
        // Economy guests: 99, 98, 97 (3 guests) - economy rooms full (0 rooms)
        // Should upgrade 2 economy guests (99, 98) to premium (total 5 premium rooms)
        assertEquals(5, response.usagePremium());
        assertEquals(150.0 + 120.0 + 100.0 + 99.0 + 98.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testOnlyEconomyRoomsAvailable() {
        // Only economy rooms, no premium rooms
        AllocationRequest request = new AllocationRequest(
                0, // premiumRooms
                3, // economyRooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 97.0, 100.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests (≥100) cannot be accommodated
        // Economy guests: 99, 98, 97 get economy rooms
        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(294.0, response.revenueEconomy()); // 99 + 98 + 97
    }

    @Test
    void testMixedWithExactUpgradeCount() {
        // Exactly enough premium rooms for all premium guests + upgrades
        AllocationRequest request = new AllocationRequest(
                5, // premiumRooms (exactly enough)
                2, // economyRooms
                Arrays.asList(200.0, 180.0, 160.0, 90.0, 85.0, 80.0, 75.0, 70.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests: 200, 180, 160 (3 guests)
        // Economy guests: 90, 85, 80, 75, 70 (5 guests)
        // Economy rooms: 2
        // Upgrades: min(5-3, 5-2) = min(2, 3) = 2 upgrades
        // Premium: 3 premium + 2 upgrades (90, 85) = 5 rooms
        // Economy: 2 rooms with 80, 75
        assertEquals(5, response.usagePremium());
        assertEquals(200.0 + 180.0 + 160.0 + 90.0 + 85.0, response.revenuePremium());
        assertEquals(2, response.usageEconomy());
        assertEquals(155.0, response.revenueEconomy()); // 80 + 75
    }

    @Test
    void testSingleGuestCases() {
        // Single premium guest
        AllocationRequest request1 = new AllocationRequest(1, 1, Arrays.asList(150.0));
        AllocationResponse response1 = roomAllocationService.calculateOptimalAllocation(request1);
        assertEquals(1, response1.usagePremium());
        assertEquals(150.0, response1.revenuePremium());
        assertEquals(0, response1.usageEconomy());
        assertEquals(0.0, response1.revenueEconomy());

        // Single economy guest
        AllocationRequest request2 = new AllocationRequest(1, 1, Arrays.asList(99.0));
        AllocationResponse response2 = roomAllocationService.calculateOptimalAllocation(request2);
        assertEquals(0, response2.usagePremium());
        assertEquals(0.0, response2.revenuePremium());
        assertEquals(1, response2.usageEconomy());
        assertEquals(99.0, response2.revenueEconomy());
    }

    // Parameterized test for multiple scenarios
    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(
                        3, 3,
                        Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0),
                        3, 738.0, 3, 167.99
                ),
                Arguments.of(
                        7, 5,
                        Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0),
                        6, 1054.0, 4, 189.99
                ),
                Arguments.of(
                        2, 7,
                        Arrays.asList(23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0),
                        2, 583.0, 4, 189.99
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testParameterized(int premiumRooms, int economyRooms, List<Double> guests,
                           int expectedPremiumUsage, double expectedPremiumRevenue,
                           int expectedEconomyUsage, double expectedEconomyRevenue) {
        AllocationRequest request = new AllocationRequest(premiumRooms, economyRooms, guests);
        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(expectedPremiumUsage, response.usagePremium());
        assertEquals(expectedPremiumRevenue, response.revenuePremium());
        assertEquals(expectedEconomyUsage, response.usageEconomy());
        assertEquals(expectedEconomyRevenue, response.revenueEconomy());
    }

    // Additional comprehensive test
    @Test
    void testComprehensiveScenario() {
        // Complex scenario to test all logic paths
        List<Double> guests = Arrays.asList(
                1000.0,  // Premium
                500.0,   // Premium
                300.0,   // Premium
                250.0,   // Premium
                200.0,   // Premium
                150.0,   // Premium
                100.0,   // Premium (boundary)
                99.99,   // Economy (highest)
                90.0,    // Economy
                80.0,    // Economy
                70.0,    // Economy
                60.0,    // Economy
                50.0,    // Economy
                40.0,    // Economy
                30.0,    // Economy
                20.0,    // Economy
                10.0     // Economy (lowest)
        );

        AllocationRequest request = new AllocationRequest(
                8,  // premiumRooms
                5,  // economyRooms
                guests
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests: 7 guests (≥100)
        // Economy guests: 10 guests (<100)
        // Premium rooms: 8, Economy rooms: 5

        // Step 1: Allocate 7 premium guests to premium rooms (1 premium room left)
        // Step 2: Economy rooms: 5 economy guests (99.99, 90, 80, 70, 60)
        // Step 3: Economy full? 10 economy guests > 5 economy rooms → Yes
        // Step 4: Upgrade available? 1 premium room left → Yes
        // Step 5: Upgrade highest economy guest not already allocated: 50.0
        // Result: Premium: 7 + 1 = 8 rooms, Economy: 5 rooms

        double expectedPremium = 1000.0 + 500.0 + 300.0 + 250.0 + 200.0 + 150.0 + 100.0 + 99.99;
        double expectedEconomy =  90.0 + 80.0 + 70.0 + 60.0 +50.0;

        assertEquals(8, response.usagePremium());
        assertEquals(expectedPremium, response.revenuePremium());
        assertEquals(5, response.usageEconomy());
        assertEquals(expectedEconomy, response.revenueEconomy());
    }


    @Test
    void testNoPremiumRooms() {
        // No premium rooms available
        AllocationRequest request = new AllocationRequest(
                0, // No premium rooms
                5, // economyRooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 97.0, 100.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests cannot be accommodated (≥100)
        // Economy guests: 99, 98, 97 get economy rooms (3 of 5 rooms filled)
        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(294.0, response.revenueEconomy()); // 99 + 98 + 97
    }

    @Test
    void testNoEconomyRooms() {
        // No economy rooms available
        AllocationRequest request = new AllocationRequest(
                5, // premiumRooms
                0, // No economy rooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 97.0, 100.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests: 150, 120, 100 (3 guests) get premium rooms
        // Economy guests: 99, 98, 97 (3 guests) - economy rooms full (0 rooms)
        // Should upgrade 2 economy guests (99, 98) to premium (total 5 premium rooms)
        assertEquals(5, response.usagePremium());
        assertEquals(150.0 + 120.0 + 100.0 + 99.0 + 98.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testNoPremiumRoomsOnlyEconomyGuests() {
        AllocationRequest request = new AllocationRequest(
                0, // No premium rooms
                3, // economyRooms
                Arrays.asList(50.0, 60.0, 70.0, 80.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(3, response.usageEconomy());
        assertEquals(80.0 + 70.0 + 60.0, response.revenueEconomy());
    }

    @Test
    void testNoEconomyRoomsOnlyPremiumGuests() {
        AllocationRequest request = new AllocationRequest(
                3, // premiumRooms
                0, // No economy rooms
                Arrays.asList(150.0, 120.0, 110.0, 100.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(3, response.usagePremium());
        assertEquals(150.0 + 120.0 + 110.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testNoRoomsAtAll() {
        AllocationRequest request = new AllocationRequest(
                0, // No premium rooms
                0, // No economy rooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testNoPremiumRoomsWithMixedGuests() {
        AllocationRequest request = new AllocationRequest(
                0, // No premium rooms
                2, // economyRooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 50.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Only economy guests (<100) can be allocated: 99, 98
        // Premium guests (≥100) cannot be accommodated
        assertEquals(0, response.usagePremium());
        assertEquals(0.0, response.revenuePremium());
        assertEquals(2, response.usageEconomy());
        assertEquals(99.0 + 98.0, response.revenueEconomy());
    }

    @Test
    void testNoEconomyRoomsWithMixedGuestsUpgradeAll() {
        AllocationRequest request = new AllocationRequest(
                5, // premiumRooms
                0, // No economy rooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 97.0, 50.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests: 150, 120 (2 guests)
        // Economy guests: 99, 98, 97, 50 (4 guests)
        // Economy rooms full (0 rooms)
        // Can upgrade up to 3 economy guests to premium (total 5 premium rooms)
        assertEquals(5, response.usagePremium());
        assertEquals(150.0 + 120.0 + 99.0 + 98.0 + 97.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

    @Test
    void testNoEconomyRoomsUpgradeSome() {
        AllocationRequest request = new AllocationRequest(
                2, // Only 2 premium rooms
                0, // No economy rooms
                Arrays.asList(150.0, 120.0, 99.0, 98.0, 97.0)
        );

        AllocationResponse response = roomAllocationService.calculateOptimalAllocation(request);

        // Premium guests: 150, 120 (2 guests) get premium rooms
        // No economy rooms, no remaining premium rooms for upgrades
        assertEquals(2, response.usagePremium());
        assertEquals(270.0, response.revenuePremium());
        assertEquals(0, response.usageEconomy());
        assertEquals(0.0, response.revenueEconomy());
    }

}