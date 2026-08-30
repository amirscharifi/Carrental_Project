package de.unikassel.se.swq.carrental.usecase.testsupport;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.CarCategory;
import de.unikassel.se.swq.carrental.model.Location;
import de.unikassel.se.swq.carrental.port.CarRepository;

import java.util.List;

public class StubCarRepository implements CarRepository {

    private final List<Car> cars;

    public StubCarRepository(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public List<Car> getAll() {
        return cars;
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
