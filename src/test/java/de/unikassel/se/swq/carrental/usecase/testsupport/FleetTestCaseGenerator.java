package de.unikassel.se.swq.carrental.usecase.testsupport;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import de.unikassel.se.swq.carrental.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FleetTestCaseGenerator extends Generator<FleetTestCase> {

    private final int maxCars = 10;
    private final int maxReservations = 8;
    private final int maxDurationDays = 35;
    private final int maxStartOffsetDays = 60;

    public FleetTestCaseGenerator() {
        super(FleetTestCase.class);
    }

    @Override
    public FleetTestCase generate(SourceOfRandomness random, GenerationStatus status) {
        List<Car> cars = generateCars(random);
        return new FleetTestCase(cars, generateReservations(random, cars), generateRentalRequest(random));

    }

    private List<Car> generateCars(SourceOfRandomness random) {
        int count = random.nextInt(0, maxCars);
        List<Car> cars = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Location location = random.choose(Location.values());
            CarCategory carCategory = random.choose(CarCategory.values());
            cars.add(new Car("car-" + i, location, carCategory));
        }

        return cars;

    }

    private List<Reservation> generateReservations(SourceOfRandomness random, List<Car> cars) {
        List<Reservation> reservations = new ArrayList<>();
        if (cars.isEmpty()) {
            return reservations;
        }
        int reservationCount = random.nextInt(0, maxReservations);
        for (int i = 0; i < reservationCount; i++) {
            Car car = random.choose(cars);
            RentalPeriod period = generateRentalPeriod(random);
            CustomerStatus status = random.choose(CustomerStatus.values());
            reservations.add(new Reservation("res-" + i, car, period, status, null));
        }
        return reservations;

    }

    private RentalRequest generateRentalRequest(SourceOfRandomness random) {
        Location pickupLocation = random.choose(Location.values());
        Location returnLocation = random.choose(Location.values());
        CarCategory carCategory = random.choose(CarCategory.values());
        CustomerStatus status = random.choose(CustomerStatus.values());
        RentalPeriod period = generateRentalPeriod(random);
        return new RentalRequest(pickupLocation, returnLocation, period, carCategory, status);

    }

    private RentalPeriod generateRentalPeriod(SourceOfRandomness random) {
        int startOffsetDays = random.nextInt(0, maxStartOffsetDays);
        int duration = random.nextInt(1, maxDurationDays);
        LocalDate startDate = LocalDate.now().plusDays(startOffsetDays);
        return new RentalPeriod(startDate, startDate.plusDays(duration));

    }

    @Override
    public List<FleetTestCase> doShrink(SourceOfRandomness random, FleetTestCase testCase) {
        List<FleetTestCase> shrinks = new ArrayList<>();

        //remove cars
        for (Car carToRemove : testCase.cars()) {
            List<Car> smallerCars = testCase.cars().stream()
                    .filter(c -> !c.id().equals(carToRemove.id()))
                    .toList();
            List<Reservation> smallerReservation = testCase.reservations().stream()
                    .filter(r -> !r.car().id().equals(carToRemove.id()))
                    .toList();
            shrinks.add(new FleetTestCase(smallerCars, smallerReservation, testCase.request()));
        }

        //remove reservations
        for (Reservation reservationToRemove : testCase.reservations()) {
            List<Reservation> smallerReservation = testCase.reservations().stream()
                    .filter(r -> !r.id().equals(reservationToRemove.id()))
                    .toList();
            shrinks.add(new FleetTestCase(testCase.cars(), smallerReservation, testCase.request()));
        }

        //shrink period
        RentalPeriod rentalPeriod = testCase.request().period();
        long days = ChronoUnit.DAYS.between(rentalPeriod.startDate, rentalPeriod.endDate);

        if (days > 1) {
            RentalPeriod shorterPeriod = new RentalPeriod(rentalPeriod.startDate, rentalPeriod.startDate.plusDays(days / 2));
            RentalRequest rentalRequest = new RentalRequest(
                    testCase.request().pickupLocation(),
                    testCase.request().returnLocation(),
                    shorterPeriod,
                    testCase.request().category(),
                    testCase.request().customerStatus());
            shrinks.add(new FleetTestCase(testCase.cars(), testCase.reservations(), rentalRequest));
        }

        return shrinks;
    }


}
