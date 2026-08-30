package de.unikassel.se.swq.carrental.usecase.testsupport;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.RentalRequest;
import de.unikassel.se.swq.carrental.model.Reservation;

import java.util.List;

public record FleetTestCase(
        List<Car> cars,
        List<Reservation> reservations,
        RentalRequest request) {
}
