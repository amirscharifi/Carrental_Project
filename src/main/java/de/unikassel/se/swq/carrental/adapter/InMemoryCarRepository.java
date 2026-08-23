package de.unikassel.se.swq.carrental.adapter;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.CarCategory;
import de.unikassel.se.swq.carrental.model.Location;
import de.unikassel.se.swq.carrental.port.CarRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCarRepository implements CarRepository {

    private final List<Car> cars = new ArrayList<>();
    private static final int CARS_PER_CATEGORY_PER_LOCATION = 2;

    public InMemoryCarRepository() {
        int counter = 1;

        for (Location location : Location.values()) {
            for (CarCategory carCategory : CarCategory.values()) {
                for (int i = 0; i < CARS_PER_CATEGORY_PER_LOCATION; i++) {
                    cars.add(new Car("car-" + counter++, location, carCategory));
                }
            }
        }
    }

    @Override
    public List<Car> getAll() {
        return List.copyOf(cars);
    }

    @Override
    public List<Car> findByLocationAndCategory(Location location, CarCategory category) {
        return cars.stream()
                .filter(car -> car.location().equals(location))
                .filter(car -> car.category().equals(category))
                .toList();
    }

    @Override
    public List<Car> findByCategory(CarCategory category) {
        return cars.stream()
                .filter(car -> car.category().equals(category))
                .toList();
    }

}
