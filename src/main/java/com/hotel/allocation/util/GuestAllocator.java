package com.hotel.allocation.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuestAllocator {

    private GuestAllocator() {
        // Utility class to prevent instantiation
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

        // Allocate premium rooms to premium guests first
        int premiumAllocated = Math.min(premiumRooms, premiumGuests.size());
        List<Double> allocatedPremiumGuests = new ArrayList<>();
        if (premiumAllocated > 0) {
            allocatedPremiumGuests.addAll(premiumGuests.subList(0, premiumAllocated));
        }

        // Calculate remaining premium rooms after allocating to premium guests
        int remainingPremiumRooms = premiumRooms - premiumAllocated;

        // Allocate economy rooms to economy guests
        int economyAllocated = Math.min(economyRooms, economyGuests.size());
        List<Double> allocatedEconomyGuests = new ArrayList<>();

        // Apply smart upgrade: if premium rooms are empty and economy rooms are full,
        // upgrade highest paying economy guests
        if (remainingPremiumRooms > 0 && economyGuests.size() > economyRooms) {
            // We can upgrade some economy guests
            int upgrades = Math.min(remainingPremiumRooms, economyGuests.size());

            // Upgrade the highest paying economy guests
            for (int i = 0; i < upgrades; i++) {
                if (i < economyGuests.size()) {
                    allocatedPremiumGuests.add(economyGuests.get(i));
                    premiumAllocated++;
                }
            }

            // Remove upgraded guests from economy list
            if (upgrades > 0 && upgrades <= economyGuests.size()) {
                economyGuests = economyGuests.subList(upgrades, economyGuests.size());
            }
        }

        // Now allocate economy rooms from remaining economy guests
        economyAllocated = Math.min(economyRooms, economyGuests.size());
        if (economyAllocated > 0) {
            allocatedEconomyGuests.addAll(economyGuests.subList(0, economyAllocated));
        }

        // Calculate revenues
        double premiumRevenue = allocatedPremiumGuests.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double economyRevenue = allocatedEconomyGuests.stream()
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