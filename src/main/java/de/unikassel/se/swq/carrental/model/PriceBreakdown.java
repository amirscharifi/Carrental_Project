package de.unikassel.se.swq.carrental.model;

public record PriceBreakdown(
        int basePrice,
        int durationPrice,
        int statusDiscount,
        int relocationFee
) {
    public int total() {
        return basePrice + durationPrice - statusDiscount + relocationFee;
    }
}
