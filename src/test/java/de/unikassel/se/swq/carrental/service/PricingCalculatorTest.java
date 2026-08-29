package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class PricingCalculatorTest {

    //------------------------------------Classification tree method------------------------------------//

    DistanceMatrix distanceMatrix = new DistanceMatrix();
    RelocationFeeCalculator relocationFeeCalculator = new RelocationFeeCalculator(distanceMatrix);
    PricingCalculator pricingCalculator = new PricingCalculator(relocationFeeCalculator, distanceMatrix);

    static Stream<Arguments> pricingCombinationTable() {
        return Stream.of(
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Silver, 1, Location.Goettingen, Location.Kassel, 58.5),
                Arguments.of(CarCategory.Van, CustomerStatus.Gold, 7, Location.Giessen, Location.Kassel, 322.75),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Standard, 30, Location.Paderborn, Location.Goettingen, 600.0)
        );
    }

    @ParameterizedTest
    @MethodSource("pricingCombinationTable")
    void calculateExpectedPriceTest(CarCategory carCategory, CustomerStatus customerStatus,
                                    int durationInDays, Location requestedLocation, Location actualLocation, double expectedPrice) {

        RentalPeriod rentalPeriod = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(durationInDays));
        Car car = new Car("car-1", actualLocation, carCategory);
        RentalRequest request = new RentalRequest(requestedLocation, requestedLocation, rentalPeriod, carCategory, customerStatus);

        Offer offer = pricingCalculator.calculateTotalPrice(car, request);

        assertEquals(expectedPrice, offer.priceBreakdown().total());
    }

    @Test
    @DisplayName("Invalid Duration")
    void throwExceptionForInvalidDuration() {

        RentalPeriod rentalPeriod = new RentalPeriod(LocalDate.now(), LocalDate.now());
        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel, rentalPeriod, CarCategory.Compact_Car, CustomerStatus.Standard);
        Car car = new Car("car-1", Location.Kassel, CarCategory.Compact_Car);

        assertThrows(IllegalArgumentException.class, () -> pricingCalculator.calculateTotalPrice(car, request));

    }

}
