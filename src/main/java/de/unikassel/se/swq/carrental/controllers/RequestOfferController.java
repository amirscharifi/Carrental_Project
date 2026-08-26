package de.unikassel.se.swq.carrental.controllers;

import de.unikassel.se.swq.carrental.model.*;
import de.unikassel.se.swq.carrental.usecase.RequestOfferUseCase;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class RequestOfferController {
    private final RequestOfferUseCase requestOfferUseCase;

    public RequestOfferController(RequestOfferUseCase requestOfferUseCase) {
        this.requestOfferUseCase = requestOfferUseCase;
    }

    @PostMapping("/request")
    public String postRequestOffer(
            @RequestParam Location pickupLocation,
            @RequestParam Location returnLocation,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam CarCategory category,
            @RequestParam CustomerStatus customerStatus,
            Model model,
            HttpSession session) {


        RentalRequest request = new RentalRequest(
                pickupLocation, returnLocation, new RentalPeriod(startDate, endDate),
                category, customerStatus);

        OfferResult result = requestOfferUseCase.evaluateRentalRequest(request);

        if (result instanceof OfferResult.Success offerResult) {
            Offer offer = offerResult.offer();
            model.addAttribute("offer", offer);
            session.setAttribute("currentOffer", offer);
            return "offer";
        } else if (result instanceof OfferResult.InvalidPeriod) {
            model.addAttribute("error", "Invalid Time Period");
        } else if (result instanceof OfferResult.NoCarAvailable) {
            model.addAttribute("error", "No Car Available");
        }

        addFormOptions(model);
        return "index";
    }

    @GetMapping("/")
    public String showRequestOffer(Model model) {
        addFormOptions(model);
        return "index";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("locations", Location.values());
        model.addAttribute("categories", CarCategory.values());
        model.addAttribute("statuses", CustomerStatus.values());
    }
}
