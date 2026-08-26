package de.unikassel.se.swq.carrental.controllers;


import de.unikassel.se.swq.carrental.model.Offer;
import de.unikassel.se.swq.carrental.model.Reservation;
import de.unikassel.se.swq.carrental.usecase.ConfirmReservationUseCase;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ConfirmReservationController {
    private final ConfirmReservationUseCase confirmReservationUseCase;

    public ConfirmReservationController(ConfirmReservationUseCase confirmReservationUseCase) {
        this.confirmReservationUseCase = confirmReservationUseCase;
    }

    @PostMapping("/reservations/confirm")
    public String confirmReservation(HttpSession session, Model model) {
        Offer offer = (Offer) session.getAttribute("currentOffer");

        if (offer == null) {
            model.addAttribute("reason", "No offer to confirm. Please request an offer first.");
            return "reservation-failed";
        }

        Optional<Reservation> reservationResult = confirmReservationUseCase.confirmReservation(offer);

        if (reservationResult.isPresent()) {
            model.addAttribute("reservation", reservationResult.get());
            session.removeAttribute("currentOffer");
            return "reservation-confirmed";
        }

        model.addAttribute("reason", "Reservation could not be confirmed.");
        return "reservation-failed";
    }

}
