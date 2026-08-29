package de.unikassel.se.swq.carrental.usecase;

import de.unikassel.se.swq.carrental.model.*;
import de.unikassel.se.swq.carrental.port.CarRepository;
import de.unikassel.se.swq.carrental.port.ReservationRepository;
import de.unikassel.se.swq.carrental.service.AvailabilityChecker;
import de.unikassel.se.swq.carrental.service.DistanceMatrix;
import de.unikassel.se.swq.carrental.service.PricingCalculator;
import de.unikassel.se.swq.carrental.service.RelocationFeeCalculator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class RequestOfferUseCaseTest {
    CarRepository carRepository = Mockito.mock(CarRepository.class);
    ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    AvailabilityChecker availabilityChecker = new AvailabilityChecker(reservationRepository, carRepository, distanceMatrix);
    RelocationFeeCalculator relocationFeeCalculator = new RelocationFeeCalculator(distanceMatrix);
    PricingCalculator pricingCalculator = new PricingCalculator(relocationFeeCalculator, distanceMatrix);

    RequestOfferUseCase requestOfferUseCase = new RequestOfferUseCase(availabilityChecker, pricingCalculator);

    @Test
    public void invalidPeriodRequestOffer() {
        List<Car> cars = List.of(new Car("car-1", Location.Kassel, CarCategory.Compact_Car));
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now());

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(cars);

        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of());

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        assertTrue(requestOfferUseCase.evaluateRentalRequest(request) instanceof OfferResult.InvalidPeriod);
    }

    @Test
    public void successfulRequestOffer() {
        List<Car> cars = List.of(new Car("car-1", Location.Kassel, CarCategory.Compact_Car));
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1));

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(cars);

        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of());

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        assertTrue(requestOfferUseCase.evaluateRentalRequest(request) instanceof OfferResult.Success);
    }

    @Test
    public void successfulWithRelocationRequestOffer() {
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1));

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(List.of());
        when(carRepository.findByCategory(CarCategory.Compact_Car))
                .thenReturn(List.of(new Car("car-2", Location.Goettingen, CarCategory.Compact_Car)));

        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of());


        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        OfferResult result = requestOfferUseCase.evaluateRentalRequest(request);

        assertTrue(result instanceof OfferResult.Success);
        assertTrue(((OfferResult.Success) result).offer().relocationRequired());
    }

    @Test
    public void noCarAvailableRequestOffer() {
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1));

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(List.of());
        when(carRepository.findByCategory(CarCategory.Compact_Car))
                .thenReturn(List.of());
        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of());

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        assertTrue(requestOfferUseCase.evaluateRentalRequest(request) instanceof OfferResult.NoCarAvailable);
    }

}
