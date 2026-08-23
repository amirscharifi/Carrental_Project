package de.unikassel.se.swq.carrental.model;

public enum CarCategory {
    Compact_Car(20),
    Station_Wagon(30),
    Van(45);

    private final double basePrice;

    CarCategory(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

}