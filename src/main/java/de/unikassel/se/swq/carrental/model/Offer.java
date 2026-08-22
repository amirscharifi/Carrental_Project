package de.unikassel.se.swq.carrental.model;

public record Offer(
        RentalRequest request,
        Car car,
        Location actualLocation,
        boolean relocationRequired,
        int relocationDistanceKm,
        PriceBreakdown priceBreakdown
) {
}
