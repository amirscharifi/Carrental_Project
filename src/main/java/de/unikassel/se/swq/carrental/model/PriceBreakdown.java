package de.unikassel.se.swq.carrental.model;

public record PriceBreakdown(
        double basePrice,
        double durationDiscount,
        double statusDiscount,
        double relocationFee
) {
    public double total() {
        return basePrice - durationDiscount - statusDiscount + relocationFee;
    }
}
