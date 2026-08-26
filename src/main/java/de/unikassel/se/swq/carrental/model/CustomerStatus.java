package de.unikassel.se.swq.carrental.model;

public enum CustomerStatus {
    Standard(0),
    Silver(0.05),
    Gold(0.10);

    private final double discountPercentage;

    CustomerStatus(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
