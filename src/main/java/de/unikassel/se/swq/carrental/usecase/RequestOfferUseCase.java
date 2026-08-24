package de.unikassel.se.swq.carrental.usecase;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.Offer;
import de.unikassel.se.swq.carrental.model.RentalRequest;
import de.unikassel.se.swq.carrental.service.AvailabilityChecker;
import de.unikassel.se.swq.carrental.service.PricingCalculator;

import java.util.Optional;

public class RequestOfferUseCase {

    private final AvailabilityChecker availabilityChecker;

    private final PricingCalculator pricingCalculator;


    public RequestOfferUseCase(AvailabilityChecker availabilityChecker, PricingCalculator pricingCalculator) {
        this.availabilityChecker = availabilityChecker;
        this.pricingCalculator = pricingCalculator;
    }

    public Optional<Offer> evaluateRentalRequest(RentalRequest request) {
        Optional<Car> availableCar = availabilityChecker.checkAvailability(request);
        if (availableCar.isPresent()) {
            return Optional.of(pricingCalculator.calculateTotalPrice(availableCar.get(), request));
        } else {
            availableCar = availabilityChecker.checkRelocationAvailability(request);
            if (availableCar.isPresent()) {
                return Optional.of(pricingCalculator.calculateTotalPrice(availableCar.get(), request));
            }
        }
        return Optional.empty();

    }

}
