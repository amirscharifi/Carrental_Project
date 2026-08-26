package de.unikassel.se.swq.carrental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalPeriod {
    public LocalDate startDate;
    public LocalDate endDate;

    public RentalPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationInDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean isValid() {
        return getDurationInDays() >= 1 && endDate.isAfter(startDate) && !startDate.isBefore(LocalDate.now());
    }

    public boolean overlapsWith(RentalPeriod other) {
        return this.startDate.isBefore(other.endDate) && other.startDate.isBefore(this.endDate);
    }

}
