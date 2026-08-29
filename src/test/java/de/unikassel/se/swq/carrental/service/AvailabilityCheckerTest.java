package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.*;
import de.unikassel.se.swq.carrental.port.CarRepository;
import de.unikassel.se.swq.carrental.port.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class AvailabilityCheckerTest {

    CarRepository carRepository = Mockito.mock(CarRepository.class);
    ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    AvailabilityChecker availabilityChecker = new AvailabilityChecker(reservationRepository, carRepository, distanceMatrix);

    @Test
    public void requestNullTest() {
        assertFalse(availabilityChecker.checkAvailability(null).isPresent());
    }

    @Test
    public void invalidPeriodTest() {
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now());
        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        assertFalse(availabilityChecker.checkAvailability(request).isPresent());
    }

    @Test
    public void checkAvailabilityTest() {
        List<Car> cars = List.of(new Car("car-1", Location.Kassel, CarCategory.Compact_Car));

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(cars);

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(3)), CarCategory.Compact_Car, CustomerStatus.Gold);

        assertTrue(availabilityChecker.checkAvailability(request).isPresent());
        assertEquals(Location.Kassel, availabilityChecker.checkAvailability(request).get().location());
        assertEquals(CarCategory.Compact_Car, availabilityChecker.checkAvailability(request).get().category());

    }

    @Test
    public void noAvailableCarsTest() {
        List<Car> cars = List.of(new Car("car-1", Location.Kassel, CarCategory.Compact_Car));
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(3));

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(cars);

        when(reservationRepository.findByCarId("car-1")).thenReturn(List.of(new Reservation("",
                new Car("car-1", Location.Kassel, CarCategory.Compact_Car), period, CustomerStatus.Gold, null)));

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        assertFalse(availabilityChecker.checkAvailability(request).isPresent());
    }

    @Test
    public void noAvailableCarsAtLocationTest() {
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(3));

        when(carRepository.findByLocationAndCategory(Location.Kassel, CarCategory.Compact_Car))
                .thenReturn(List.of());

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);

        assertFalse(availabilityChecker.checkAvailability(request).isPresent());
    }

    @Test
    public void checkRelocationAvailabilityTest() {
        List<Car> cars = List.of(new Car("car-1", Location.Giessen, CarCategory.Compact_Car),
                new Car("car-2", Location.Goettingen, CarCategory.Compact_Car));
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(3));

        when(carRepository.findByCategory(CarCategory.Compact_Car))
                .thenReturn(cars);

        RentalRequest request = new RentalRequest(Location.Kassel, Location.Kassel,
                period, CarCategory.Compact_Car, CustomerStatus.Gold);


        assertTrue(availabilityChecker.checkRelocationAvailability(request).isPresent());
        //actual location should be Goettingen since the distance for the relocation from Kassel to Goettingen is shorter than Giessen
        assertEquals(Location.Goettingen, availabilityChecker.checkRelocationAvailability(request).get().location());
        assertEquals(CarCategory.Compact_Car, availabilityChecker.checkRelocationAvailability(request).get().category());

    }

    @Test
    public void carStillAvailableTest() {
        Car car = new Car("car-1", Location.Giessen, CarCategory.Compact_Car);
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(3));

        when(reservationRepository.findByCarId("car-1"))
                .thenReturn(List.of(new Reservation("", car,
                        period, CustomerStatus.Gold, null)));

        assertFalse(availabilityChecker.carStillAvailable(car, period));

    }

    @Test
    public void noCarStillAvailableTest() {
        Car car = new Car("car-1", Location.Giessen, CarCategory.Compact_Car);
        RentalPeriod period = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(3));

        when(reservationRepository.findByCarId("car-1"))
                .thenReturn(List.of());

        assertTrue(availabilityChecker.carStillAvailable(car, period));

    }


}
