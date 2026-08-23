package de.unikassel.se.swq.carrental.adapter;

import de.unikassel.se.swq.carrental.model.Reservation;
import de.unikassel.se.swq.carrental.port.ReservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryReservationRepository implements ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();

    @Override
    public List<Reservation> findByCarId(String carId) {
        return reservations.stream().filter(reservation -> reservation.car().id().equals(carId)).toList();
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.id() == null) {
            reservation = new Reservation(UUID.randomUUID().toString(),
                    reservation.car(), reservation.period(), reservation.customerStatus(), reservation.priceBreakdown());
        }

        reservations.add(reservation);
        return reservation;

    }

}
