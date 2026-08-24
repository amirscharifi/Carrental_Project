package de.unikassel.se.swq.carrental.controllers;

import de.unikassel.se.swq.carrental.model.CarCategory;
import de.unikassel.se.swq.carrental.model.CustomerStatus;
import de.unikassel.se.swq.carrental.model.Location;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequestOfferController {

    public RequestOfferController() {

    }

    @GetMapping("/")
    public String showRequestOffer(Model model){
        addFormOptions(model);
        return "index.html";
    }

    private void addFormOptions(Model model){
        model.addAttribute("locations", Location.values());
        model.addAttribute("categories", CarCategory.values());
        model.addAttribute("statuses", CustomerStatus.values());
    }
}
