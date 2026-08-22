package de.unikassel.se.swq.carrental.model;

public record RentalRequest(Location pickupLocation,
                            Location returnLocation,
                            RentalPeriod period,
                            CarCategory category,
                            CustomerStatus customerStatus) {
}
