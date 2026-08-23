package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.Offer;
import de.unikassel.se.swq.carrental.model.PriceBreakdown;
import de.unikassel.se.swq.carrental.model.RentalRequest;

public class PricingCalculator {

    RelocationFeeCalculator relocationFeeCalculator;
    DistanceMatrix distanceMatrix;

    public PricingCalculator(RelocationFeeCalculator relocationFeeCalculator, DistanceMatrix distanceMatrix) {
        this.relocationFeeCalculator = relocationFeeCalculator;
        this.distanceMatrix = distanceMatrix;
    }

    public Offer calculateTotalPrice(Car car, RentalRequest request) {
        double basePrice = car.category().getBasePrice() * request.period().getDurationInDays();
        double durationDiscount = basePrice * getDurationDiscount(request);
        double statusDiscount = request.customerStatus().getDiscountPercentage() * basePrice;
        double relocationfee = 0.0;
        int relocationDistance = distanceMatrix.getDistance(request.pickupLocation(), car.location());
        boolean relocationRequired = relocationDistance != 0;

        if (relocationRequired){
            relocationfee = relocationFeeCalculator.calculateRelocationFee(request.pickupLocation(), car.location());
        }

        PriceBreakdown priceBreakdown = new PriceBreakdown(basePrice, durationDiscount, statusDiscount, relocationfee);

        return new Offer(request, car, car.location(), relocationRequired,
               relocationDistance, priceBreakdown);
    }

    private double getDurationDiscount(RentalRequest request) {
        int durationInDays = request.period().getDurationInDays();
        double durationDiscount = 0.0;

        if (durationInDays < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 day");
        } else if (durationInDays == 1) {
            durationDiscount = 0.0;
        } else if (durationInDays <= 7) {
            durationDiscount = 0.05;
        } else if (durationInDays <= 30) {
            durationDiscount = 0.15;
        } else {
            durationDiscount = 0.25;
        }

        return durationDiscount;

    }

}
