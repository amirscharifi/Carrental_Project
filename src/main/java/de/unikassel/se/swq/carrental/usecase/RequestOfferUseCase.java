package de.unikassel.se.swq.carrental.usecase;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.OfferResult;
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

    public OfferResult evaluateRentalRequest(RentalRequest request) {
        if (!request.period().isValid()) {
            return new OfferResult.InvalidPeriod();
        }

        Optional<Car> availableCar = availabilityChecker.checkAvailability(request);
        if (availableCar.isPresent()) {
            return new OfferResult.Success(pricingCalculator.calculateTotalPrice(availableCar.get(), request));
        } else {
            availableCar = availabilityChecker.checkRelocationAvailability(request);
            if (availableCar.isPresent()) {
                return new OfferResult.Success(pricingCalculator.calculateTotalPrice(availableCar.get(), request));
            }
        }
        return new OfferResult.NoCarAvailable();

    }

}
