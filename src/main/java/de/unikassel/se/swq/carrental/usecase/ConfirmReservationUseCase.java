package de.unikassel.se.swq.carrental.usecase;

import de.unikassel.se.swq.carrental.model.Offer;
import de.unikassel.se.swq.carrental.model.Reservation;
import de.unikassel.se.swq.carrental.port.ReservationRepository;
import de.unikassel.se.swq.carrental.service.AvailabilityChecker;

import java.util.Optional;

public class ConfirmReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final AvailabilityChecker availabilityChecker;

    public ConfirmReservationUseCase(ReservationRepository reservationRepository, AvailabilityChecker availabilityChecker) {
        this.reservationRepository = reservationRepository;
        this.availabilityChecker = availabilityChecker;
    }

    public Optional<Reservation> confirmReservation(Offer offer) {
        boolean stillAvailable = availabilityChecker.carStillAvailable(offer.car(), offer.request().period());

        if (stillAvailable) {
            Reservation reservation = new Reservation(null, offer.car(), offer.request().period(),
                    offer.request().customerStatus(), offer.priceBreakdown());

            reservationRepository.save(reservation);
            return Optional.of(reservation);
        }

        return Optional.empty();

    }


}
