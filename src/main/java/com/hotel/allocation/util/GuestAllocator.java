package com.hotel.allocation.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuestAllocator {

    private GuestAllocator() {
        // Utility class - prevent instantiation
    }

    public static AllocationResult allocateRooms(int premiumRooms, int economyRooms, List<Double> potentialGuests) {
        // Separate guests into premium and economy categories
        List<Double> premiumGuests = new ArrayList<>();
        List<Double> economyGuests = new ArrayList<>();

        for (Double price : potentialGuests) {
            if (price >= 100.0) {
                premiumGuests.add(price);
            } else {
                economyGuests.add(price);
            }
        }

        // Sort in descending order to prioritize higher paying guests
        premiumGuests.sort(Comparator.reverseOrder());
        economyGuests.sort(Comparator.reverseOrder());

        // Special case: No premium rooms available
        if (premiumRooms == 0) {
            return handleNoPremiumRooms(economyRooms, economyGuests);
        }

        // Special case: No economy rooms available
        if (economyRooms == 0) {
            return handleNoEconomyRooms(premiumRooms, premiumGuests, economyGuests);
        }

        return handleGeneralCase(premiumRooms, economyRooms, premiumGuests, economyGuests);
    }

    private static AllocationResult handleNoPremiumRooms(int economyRooms, List<Double> economyGuests) {
        // No premium rooms, only allocate economy guests to economy rooms
        int economyAllocated = Math.min(economyRooms, economyGuests.size());
        List<Double> allocatedEconomy = economyGuests.subList(0, economyAllocated);
        double economyRevenue = allocatedEconomy.stream().mapToDouble(Double::doubleValue).sum();

        return new AllocationResult(
                0, // premiumAllocated
                0.0, // premiumRevenue
                economyAllocated,
                Math.round(economyRevenue * 100.0) / 100.0
        );
    }

    private static AllocationResult handleNoEconomyRooms(int premiumRooms, List<Double> premiumGuests, List<Double> economyGuests) {
        // No economy rooms
        // First allocate premium guests to premium rooms
        int premiumAllocated = Math.min(premiumRooms, premiumGuests.size());
        List<Double> allocatedPremium = premiumGuests.subList(0, premiumAllocated);

        int remainingPremiumRooms = premiumRooms - premiumAllocated;

        // Economy rooms are "full" (since we have 0 economy rooms, any economy guest exceeds capacity)
        // So we can upgrade economy guests to premium rooms if premium rooms are available
        if (remainingPremiumRooms > 0 && !economyGuests.isEmpty()) {
            // Upgrade the highest paying economy guests
            int upgrades = Math.min(remainingPremiumRooms, economyGuests.size());
            for (int i = 0; i < upgrades; i++) {
                allocatedPremium.add(economyGuests.get(i));
                premiumAllocated++;
            }
        }

        double premiumRevenue = allocatedPremium.stream().mapToDouble(Double::doubleValue).sum();

        return new AllocationResult(
                premiumAllocated,
                Math.round(premiumRevenue * 100.0) / 100.0,
                0, // economyAllocated
                0.0 // economyRevenue
        );
    }

    private static AllocationResult handleGeneralCase(int premiumRooms, int economyRooms,
                                                      List<Double> premiumGuests, List<Double> economyGuests) {
        // STEP 1: Allocate premium rooms to premium guests
        int premiumAllocated = Math.min(premiumRooms, premiumGuests.size());
        List<Double> allocatedPremium = new ArrayList<>(premiumGuests.subList(0, premiumAllocated));

        // STEP 2: Calculate remaining premium rooms
        int remainingPremiumRooms = premiumRooms - premiumAllocated;

        // STEP 3: Allocate economy rooms to economy guests
        int economyAllocated = Math.min(economyRooms, economyGuests.size());
        List<Double> allocatedEconomy = new ArrayList<>();

        // STEP 4: Check if upgrade is possible
        // Upgrade condition: Premium rooms are available AND economy rooms are full
        // AND there are more economy guests than economy rooms
        if (remainingPremiumRooms > 0 && economyGuests.size() > economyRooms) {
            // We can upgrade some economy guests
            int upgrades = Math.min(remainingPremiumRooms, economyGuests.size() - economyRooms);

            // Upgrade the highest paying economy guests
            for (int i = 0; i < upgrades; i++) {
                allocatedPremium.add(economyGuests.get(i));
                premiumAllocated++;
            }

            // Remove upgraded guests from economy list
            if (upgrades > 0 && upgrades <= economyGuests.size()) {
                economyGuests = economyGuests.subList(upgrades, economyGuests.size());
            }
        }

        // STEP 5: Now allocate economy rooms from remaining economy guests
        economyAllocated = Math.min(economyRooms, economyGuests.size());
        if (economyAllocated > 0) {
            allocatedEconomy.addAll(economyGuests.subList(0, economyAllocated));
        }

        // STEP 6: Calculate revenues
        double premiumRevenue = allocatedPremium.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double economyRevenue = allocatedEconomy.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Round to 2 decimal places
        premiumRevenue = Math.round(premiumRevenue * 100.0) / 100.0;
        economyRevenue = Math.round(economyRevenue * 100.0) / 100.0;

        return new AllocationResult(
                premiumAllocated,
                premiumRevenue,
                economyAllocated,
                economyRevenue
        );
    }

    // Helper method for debugging
    public static void debugAllocation(int premiumRooms, int economyRooms, List<Double> potentialGuests) {
        System.out.println("=== DEBUG ALLOCATION ===");
        System.out.println("Premium Rooms: " + premiumRooms);
        System.out.println("Economy Rooms: " + economyRooms);
        System.out.println("Guests: " + potentialGuests);

        // Separate guests
        List<Double> premiumGuests = new ArrayList<>();
        List<Double> economyGuests = new ArrayList<>();

        for (Double price : potentialGuests) {
            if (price >= 100.0) {
                premiumGuests.add(price);
            } else {
                economyGuests.add(price);
            }
        }

        premiumGuests.sort(Comparator.reverseOrder());
        economyGuests.sort(Comparator.reverseOrder());

        System.out.println("Premium Guests (sorted): " + premiumGuests);
        System.out.println("Economy Guests (sorted): " + economyGuests);

        // Run allocation
        AllocationResult result = allocateRooms(premiumRooms, economyRooms, potentialGuests);

        System.out.println("Result:");
        System.out.println("  Premium Allocated: " + result.getPremiumAllocated());
        System.out.println("  Premium Revenue: " + result.getPremiumRevenue());
        System.out.println("  Economy Allocated: " + result.getEconomyAllocated());
        System.out.println("  Economy Revenue: " + result.getEconomyRevenue());
        System.out.println("=== END DEBUG ===");
    }

    public static class AllocationResult {
        private final int premiumAllocated;
        private final double premiumRevenue;
        private final int economyAllocated;
        private final double economyRevenue;

        public AllocationResult(int premiumAllocated, double premiumRevenue,
                                int economyAllocated, double economyRevenue) {
            this.premiumAllocated = premiumAllocated;
            this.premiumRevenue = premiumRevenue;
            this.economyAllocated = economyAllocated;
            this.economyRevenue = economyRevenue;
        }

        public int getPremiumAllocated() {
            return premiumAllocated;
        }

        public double getPremiumRevenue() {
            return premiumRevenue;
        }

        public int getEconomyAllocated() {
            return economyAllocated;
        }

        public double getEconomyRevenue() {
            return economyRevenue;
        }
    }
}