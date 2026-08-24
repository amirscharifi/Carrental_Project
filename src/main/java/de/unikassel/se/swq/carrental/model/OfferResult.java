package de.unikassel.se.swq.carrental.model;

public sealed interface OfferResult {

    // Represents the outcome of an offer request, allowing the caller to distinguish
    // between a successful offer and the specific reason it could not be created.

    record Success(Offer offer) implements OfferResult {
    }

    record InvalidPeriod() implements OfferResult {
    }

    record NoCarAvailable() implements OfferResult {
    }
}
