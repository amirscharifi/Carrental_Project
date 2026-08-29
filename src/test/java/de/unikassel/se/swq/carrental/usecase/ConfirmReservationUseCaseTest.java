package de.unikassel.se.swq.carrental.usecase;

import de.unikassel.se.swq.carrental.model.*;
import de.unikassel.se.swq.carrental.port.CarRepository;
import de.unikassel.se.swq.carrental.port.ReservationRepository;
import de.unikassel.se.swq.carrental.service.AvailabilityChecker;
import de.unikassel.se.swq.carrental.service.DistanceMatrix;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ConfirmReservationUseCaseTest {

    CarRepository carRepository = Mockito.mock(CarRepository.class);
    ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    AvailabilityChecker availabilityChecker = new AvailabilityChecker(reservationRepository, carRepository, distanceMatrix);
    ConfirmReservationUseCase confirmReservationUseCase = new ConfirmReservationUseCase(reservationRepository, availabilityChecker);

    @Test
    public void confirmReservationFailedTest() {
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1));
        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel, period, CarCategory.Compact_Car, CustomerStatus.Gold);
        Car car = new Car("car-1", Location.Kassel, CarCategory.Compact_Car);

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(List.of(car));

        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of(new Reservation("",
                car, period, CustomerStatus.Gold, null)));


        assertTrue(confirmReservationUseCase.confirmReservation(
                new Offer(request, car, car.location(), false, 0, null)).isEmpty());

    }

    @Test
    public void confirmReservationSuccessfullyTest() {
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1));
        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel, period, CarCategory.Compact_Car, CustomerStatus.Gold);
        Car car = new Car("car-1", Location.Kassel, CarCategory.Compact_Car);
        Reservation reservation = new Reservation("", car, period, CustomerStatus.Gold, null);

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(List.of(car));

        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);


        Offer offer = new Offer(request, car, car.location(), false, 0, null);
        Optional<Reservation> resultReservation = confirmReservationUseCase.confirmReservation(offer);

        assertTrue(resultReservation.isPresent());
        assertEquals(resultReservation.get().car(), car);

    }

}
