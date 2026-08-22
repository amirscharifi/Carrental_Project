package de.unikassel.se.swq.carrental.port;

import de.unikassel.se.swq.carrental.model.Reservation;

import java.util.List;

public interface ReservationRepository {

    List<Reservation> findByCarId(String carId);

    Reservation save(Reservation reservation);

}
