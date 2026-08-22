package de.unikassel.se.swq.carrental.port;

import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.CarCategory;
import de.unikassel.se.swq.carrental.model.Location;

import java.util.List;

public interface CarRepository {

    List<Car> getAll();

    List<Car> findByLocationAndCategory(Location location, CarCategory category);

    List<Car> findByCategory(CarCategory category);

}
