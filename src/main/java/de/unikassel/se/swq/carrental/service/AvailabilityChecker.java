package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.RentalRequest;
import de.unikassel.se.swq.carrental.model.Reservation;
import de.unikassel.se.swq.carrental.port.CarRepository;
import de.unikassel.se.swq.carrental.port.ReservationRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AvailabilityChecker {

    ReservationRepository reservationRepository;
    CarRepository carRepository;
    DistanceMatrix distanceMatrix;

    public AvailabilityChecker(ReservationRepository reservationRepository, CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.distanceMatrix = new DistanceMatrix();
    }

    public Optional<Car> checkAvailability(RentalRequest request) {
        //checks the availability of a requested car at the requested location
        if (request == null) {
            return Optional.empty();
        }
        if (!request.period().isValid()) {
            return Optional.empty();
        }

        List<Car> cars = carRepository.findByLocationAndCategory(request.pickupLocation(), request.category());

        if (cars.isEmpty()) {
            return Optional.empty();
        }

        for (Car car : cars) {
            List<Reservation> reservation = reservationRepository.findByCarId(car.id());

            if (reservation.stream().noneMatch(element -> element.period().overlapsWith(request.period()))) {
                return Optional.of(car);
            }

        }

        return Optional.empty();

    }

    public Optional<Car> checkRelocationAvailability(RentalRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        if (!request.period().isValid()) {
            return Optional.empty();
        }

        List<Car> cars = carRepository.findByCategory(request.category());
        List<Car> availableCars = new ArrayList<>();

        for (Car car : cars) {
            List<Reservation> reservation = reservationRepository.findByCarId(car.id());

            if (reservation.stream().noneMatch(element -> element.period().overlapsWith(request.period()))) {
                availableCars.add(car);
            }

        }

        return availableCars.stream().min(Comparator.comparing(car -> distanceMatrix.getDistance(car.location(), request.pickupLocation())));


    }

}
