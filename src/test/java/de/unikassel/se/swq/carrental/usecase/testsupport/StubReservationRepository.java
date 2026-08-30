package de.unikassel.se.swq.carrental.usecase.testsupport;

import de.unikassel.se.swq.carrental.model.Reservation;
import de.unikassel.se.swq.carrental.port.ReservationRepository;

import java.util.List;
import java.util.UUID;

public class StubReservationRepository implements ReservationRepository {
    private final List<Reservation> reservations;

    public StubReservationRepository(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public List<Reservation> findByCarId(String carId) {
        return reservations.stream().filter(reservation -> reservation.car().id().equals(carId)).toList();
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservation;

    }

}
