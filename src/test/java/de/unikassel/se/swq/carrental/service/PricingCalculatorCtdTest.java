package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PricingCalculatorCtdTest {

    //------------------------------------Combinatorial Test Design------------------------------------//

    DistanceMatrix distanceMatrix = new DistanceMatrix();
    RelocationFeeCalculator relocationFeeCalculator = new RelocationFeeCalculator(distanceMatrix);
    PricingCalculator pricingCalculator = new PricingCalculator(relocationFeeCalculator, distanceMatrix);

    static Stream<Arguments> ctdPriceCombinationTable() {
        return Stream.of(
                Arguments.of(CarCategory.Van, CustomerStatus.Standard, 1, Location.Kassel, Location.Kassel, 45.0),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Silver, 1, Location.Kassel, Location.Goettingen, 49.0),
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Gold, 1, Location.Kassel, Location.Paderborn, 82.0),
                Arguments.of(CarCategory.Van, CustomerStatus.Silver, 1, Location.Goettingen, Location.Giessen, 132.75),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Gold, 5, Location.Kassel, Location.Kassel, 85.0),
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Standard, 5, Location.Kassel, Location.Goettingen, 172.5),
                Arguments.of(CarCategory.Van, CustomerStatus.Silver, 5, Location.Kassel, Location.Paderborn, 257.5),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Standard, 5, Location.Goettingen, Location.Giessen, 185.0),
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Silver, 15, Location.Kassel, Location.Kassel, 360.0),
                Arguments.of(CarCategory.Van, CustomerStatus.Gold, 15, Location.Kassel, Location.Goettingen, 536.25),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Standard, 15, Location.Kassel, Location.Paderborn, 310.0),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Silver, 15, Location.Goettingen, Location.Giessen, 330.0),
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Silver, 35, Location.Kassel, Location.Kassel, 735.0),
                Arguments.of(CarCategory.Van, CustomerStatus.Gold, 35, Location.Kassel, Location.Goettingen, 1053.75),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Standard, 35, Location.Kassel, Location.Paderborn, 580.0),
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Gold, 35, Location.Goettingen, Location.Giessen, 772.5)
        );
    }

    @ParameterizedTest
    @MethodSource("ctdPriceCombinationTable")
    void calculateExpectedPriceTest(CarCategory carCategory, CustomerStatus customerStatus,
                                    int durationInDays, Location requestedLocation, Location actualLocation, double expectedPrice) {

        RentalPeriod rentalPeriod = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(durationInDays));
        Car car = new Car("car-1", actualLocation, carCategory);
        RentalRequest request = new RentalRequest(requestedLocation, requestedLocation, rentalPeriod, carCategory, customerStatus);

        Offer offer = pricingCalculator.calculateTotalPrice(car, request);

        assertEquals(expectedPrice, offer.priceBreakdown().total());
    }

    static Stream<Arguments> ctdInvalidDurationCombinationTable() {
        return Stream.of(
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Silver, Location.Kassel, Location.Kassel),
                Arguments.of(CarCategory.Van, CustomerStatus.Gold, Location.Kassel, Location.Goettingen),
                Arguments.of(CarCategory.Compact_Car, CustomerStatus.Standard, Location.Kassel, Location.Paderborn),
                Arguments.of(CarCategory.Station_Wagon, CustomerStatus.Gold, Location.Goettingen, Location.Giessen)
        );
    }

    @ParameterizedTest
    @MethodSource("ctdInvalidDurationCombinationTable")
    void throwExceptionForInvalidDuration(CarCategory carCategory, CustomerStatus customerStatus,
                                          Location requestedLocation, Location actualLocation) {

        RentalPeriod rentalPeriod = new RentalPeriod(LocalDate.now(), LocalDate.now());
        RentalRequest request = new RentalRequest(requestedLocation, requestedLocation, rentalPeriod, carCategory, customerStatus);
        Car car = new Car("car-1", actualLocation, carCategory);

        assertThrows(IllegalArgumentException.class, () -> pricingCalculator.calculateTotalPrice(car, request));

    }

}
