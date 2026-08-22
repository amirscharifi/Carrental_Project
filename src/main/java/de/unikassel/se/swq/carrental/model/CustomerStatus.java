package de.unikassel.se.swq.carrental.model;

public enum CustomerStatus {
    STANDARD(0),
    SILVER(0.05),
    GOLD(0.10);

    private final double discountPercentage;

    CustomerStatus(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
