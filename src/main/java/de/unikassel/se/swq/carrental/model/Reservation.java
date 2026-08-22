package de.unikassel.se.swq.carrental.model;

public record Reservation(
        String id,
        Car car,
        RentalPeriod period,
        CustomerStatus customerStatus,
        PriceBreakdown priceBreakdown
) {
}
