package de.unikassel.se.swq.carrental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalPeriod {
    LocalDate startDate;
    LocalDate endDate;

    RentalPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getDurationInDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean isValid() {
        return getDurationInDays() >= 1 && endDate.isAfter(startDate);
    }

    public boolean overlapsWith(RentalPeriod other) {
        return this.startDate.isBefore(other.endDate) && other.startDate.isBefore(this.endDate);
    }

}
